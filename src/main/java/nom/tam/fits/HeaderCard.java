package nom.tam.fits;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.regex.Pattern;

import javax.management.RuntimeErrorException;

import nom.tam.util.ArrayDataInput;
import nom.tam.util.AsciiFuncs;
import nom.tam.util.BufferedDataInputStream;

/*
 * #%L
 * nom.tam FITS library
 * %%
 * Copyright (C) 2004 - 2015 nom-tam-fits
 * %%
 * This is free and unencumbered software released into the public domain.
 * 
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 * 
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 * #L%
 */

/**
 * This class describes methods to access and manipulate the individual cards
 * for a FITS Header.
 */
public class HeaderCard {

    private static final BigDecimal LONG_MAX_VALUE_AS_BIG_DECIMAL = BigDecimal.valueOf(Long.MAX_VALUE);

    /**
     * regexp for IEEE floats
     */
    private static final Pattern IEEE_REGEX = Pattern.compile("[+-]?(?=\\d*[.eE])(?=\\.?\\d)\\d*\\.?\\d*(?:[eE][+-]?\\d+)?");

    /**
     * regexp for numbers.
     */
    private static final Pattern LONG_REGEX = Pattern.compile("[+-]?[0-9][0-9]*");

    /**
     * max number of characters an integer can have.
     */
    private static final int MAX_INTEGER_STRING_SIZE = Integer.valueOf(Integer.MAX_VALUE).toString().length() - 1;

    /**
     * max number of characters a long can have.
     */
    private static final int MAX_LONG_STRING_SIZE = Long.valueOf(Long.MAX_VALUE).toString().length() - 1;

    /** The keyword part of the card (set to null if there's no keyword) */
    private String key;

    /** The value part of the card (set to null if there's no value) */
    private String value;

    /** The comment part of the card (set to null if there's no comment) */
    private String comment;

    /** Does this card represent a nullable field. ? */
    private boolean nullable;

    /** A flag indicating whether or not this is a string value */
    private boolean isString;

    /** Maximum length of a FITS keyword field */
    public static final int MAX_KEYWORD_LENGTH = 8;

    /**
     * Maximum length of a FITS value field
     */
    public static final int MAX_VALUE_LENGTH = 70;

    /**
     * Maximum length of a FITS string value field
     */
    public static final int MAX_STRING_VALUE_LENGTH = MAX_VALUE_LENGTH - 2;

    /** padding for building card images */
    private static String space80 = "                                                                                ";

    /**
     * Create a HeaderCard from its component parts
     * 
     * @param key
     *            keyword (null for a comment)
     * @param value
     *            value (null for a comment or keyword without an '=')
     * @param comment
     *            comment
     * @exception HeaderCardException
     *                for any invalid keyword
     */
    public HeaderCard(String key, double value, String comment) throws HeaderCardException {
        this(key, dblString(value), comment, false, false);
    }

    /**
     * Create a HeaderCard from its component parts
     * 
     * @param key
     *            keyword (null for a comment)
     * @param value
     *            value (null for a comment or keyword without an '=')
     * @param comment
     *            comment
     * @exception HeaderCardException
     *                for any invalid keyword
     */
    public HeaderCard(String key, float value, String comment) throws HeaderCardException {
        this(key, dblString(value), comment, false, false);
    }

    /**
     * Create a HeaderCard from its component parts
     * 
     * @param key
     *            keyword (null for a comment)
     * @param value
     *            value (null for a comment or keyword without an '=')
     * @param comment
     *            comment
     * @exception HeaderCardException
     *                for any invalid keyword
     */
    public HeaderCard(String key, BigDecimal value, String comment) throws HeaderCardException {
        this(key, dblString(value), comment, false, false);
    }

    /**
     * Create a HeaderCard from its component parts
     * 
     * @param key
     *            keyword (null for a comment)
     * @param value
     *            value (null for a comment or keyword without an '=')
     * @param comment
     *            comment
     * @exception HeaderCardException
     *                for any invalid keyword
     */
    public HeaderCard(String key, BigInteger value, String comment) throws HeaderCardException {
        this(key, dblString(new BigDecimal(value)), comment, false, false);
    }

    /**
     * Create a HeaderCard from its component parts
     * 
     * @param key
     *            keyword (null for a comment)
     * @param value
     *            value (null for a comment or keyword without an '=')
     * @param comment
     *            comment
     * @exception HeaderCardException
     *                for any invalid keyword
     */
    public HeaderCard(String key, boolean value, String comment) throws HeaderCardException {
        this(key, value ? "T" : "F", comment, false, false);
    }

    /**
     * Create a HeaderCard from its component parts
     * 
     * @param key
     *            keyword (null for a comment)
     * @param value
     *            value (null for a comment or keyword without an '=')
     * @param comment
     *            comment
     * @exception HeaderCardException
     *                for any invalid keyword
     */
    public HeaderCard(String key, int value, String comment) throws HeaderCardException {
        this(key, String.valueOf(value), comment, false, false);
    }

    /**
     * Create a HeaderCard from its component parts
     * 
     * @param key
     *            keyword (null for a comment)
     * @param value
     *            value (null for a comment or keyword without an '=')
     * @param comment
     *            comment
     * @exception HeaderCardException
     *                for any invalid keyword
     */
    public HeaderCard(String key, long value, String comment) throws HeaderCardException {
        this(key, String.valueOf(value), comment, false, false);
    }

    /**
     * Create a HeaderCard from its component parts
     * 
     * @param key
     *            keyword (null for a comment)
     * @param value
     *            value (null for a comment or keyword without an '=')
     * @param comment
     *            comment
     * @exception HeaderCardException
     *                for any invalid keyword or value
     */
    public HeaderCard(String key, String value, String comment) throws HeaderCardException {
        this(key, value, comment, false, true);
    }

    /**
     * Create a comment style card. This constructor builds a card which has no
     * value. This may be either a comment style card in which case the nullable
     * field should be false, or a value field which has a null value, in which
     * case the nullable field should be true.
     * 
     * @param key
     *            The key for the comment or nullable field.
     * @param comment
     *            The comment
     * @param nullable
     *            Is this a nullable field or a comment-style card?
     */
    public HeaderCard(String key, String comment, boolean nullable) throws HeaderCardException {
        this(key, null, comment, nullable, true);
    }

    /**
     * Create a string from a double making sure that it's not more than 20
     * characters long. Probably would be better if we had a way to override
     * this since we can loose precision for some doubles.
     */
    private static String dblString(double input) {
        String value = Double.toString(input);
        if (value.length() > 20) {
            return dblString(BigDecimal.valueOf(input));
        }
        return value;
    }

    /**
     * Create a string from a BigDecimal making sure that it's not more than 20
     * characters long. Probably would be better if we had a way to override
     * this since we can loose precision for some doubles.
     */
    private static String dblString(BigDecimal input) {
        String value = input.toString();
        BigDecimal decimal = input;
        while (value.length() > 20) {
            decimal = input.setScale(decimal.scale() - 1, BigDecimal.ROUND_HALF_UP);
            value = decimal.toString();
        }
        return value;
    }

    /**
     * Create a HeaderCard from its component parts
     * 
     * @param key
     *            Keyword (null for a COMMENT)
     * @param value
     *            Value
     * @param comment
     *            Comment
     * @param nullable
     *            Is this a nullable value card?
     * @exception HeaderCardException
     *                for any invalid keyword or value
     */
    public HeaderCard(String key, String value, String comment, boolean nullable) throws HeaderCardException {
        this(key, value, comment, nullable, true);
    }

    /**
     * Create a HeaderCard from its component parts
     * 
     * @param key
     *            Keyword (null for a COMMENT)
     * @param value
     *            Value
     * @param comment
     *            Comment
     * @param nullable
     *            Is this a nullable value card?
     * @exception HeaderCardException
     *                for any invalid keyword or value
     */
    private HeaderCard(String key, String value, String comment, boolean nullable, boolean isString) throws HeaderCardException {
        this.isString = isString;
        if (comment != null && comment.startsWith("ntf::")) {
            String ckey = comment.substring(5); // Get rid of ntf:: prefix
            comment = HeaderCommentsMap.getComment(ckey);
        }
        if (key == null && value != null) {
            throw new HeaderCardException("Null keyword with non-null value");
        }

        if (key != null && key.length() > MAX_KEYWORD_LENGTH) {
            if (!FitsFactory.getUseHierarch() || !key.substring(0, 9).equals("HIERARCH.")) {
                throw new HeaderCardException("Keyword too long");
            }
        }

        if (value != null) {
            value = value.replaceAll(" *$", "");

            if (value.startsWith("'")) {
                if (value.charAt(value.length() - 1) != '\'') {
                    throw new HeaderCardException("Missing end quote in string value");
                }
                value = value.substring(1, value.length() - 1).trim();
            }
            // Remember that quotes get doubled in the value...
            if (!FitsFactory.isLongStringsEnabled() && value.replace("'", "''").length() > (this.isString ? MAX_STRING_VALUE_LENGTH : MAX_VALUE_LENGTH)) {
                throw new HeaderCardException("Value too long");
            }

        }

        this.key = key;
        this.value = value;
        this.comment = comment;
        this.nullable = nullable;
    }

    /**
     * Create a HeaderCard from a FITS card image
     * 
     * @param card
     *            the 80 character card image
     * @throws IOException
     * @throws TruncatedFileException
     */
    public static HeaderCard create(String card) {
        try {
            return new HeaderCard(stringToArrayInputStream(card));
        } catch (Exception e) {
            throw new IllegalArgumentException("card not legal", e);
        }
    }

    private static ArrayDataInput stringToArrayInputStream(String card) {
        byte[] bytes = AsciiFuncs.getBytes(card);
        if ((bytes.length % 80) != 0) {
            byte[] newBytes = new byte[bytes.length + 80 - (bytes.length % 80)];
            System.arraycopy(bytes, 0, newBytes, 0, bytes.length);
            Arrays.fill(newBytes, bytes.length, newBytes.length, (byte) ' ');
            bytes = newBytes;
        }
        return new BufferedDataInputStream(new ByteArrayInputStream(bytes));
    }

    private String readOneHeaderLine(ArrayDataInput dis) throws IOException, TruncatedFileException, EOFException {
        byte[] buffer = new byte[80];
        int len;
        int need = 80;
        try {

            while (need > 0) {
                len = dis.read(buffer, 80 - need, need);
                if (len == 0) {
                    throw new TruncatedFileException();
                }
                need -= len;
            }
        } catch (EOFException e) {

            // Rethrow the EOF if we are at the beginning of the header,
            // otherwise we have a FITS error.
            // Added by Booth Hartley:
            // If this is an extension HDU, then we may allow
            // junk at the end and simply ignore it
            //
            if (need == 80 || FitsFactory.getAllowTerminalJunk()) {
                throw e;
            }
            throw new TruncatedFileException(e.getMessage());
        }
        return AsciiFuncs.asciiString(buffer);
    }

    public HeaderCard(ArrayDataInput dis) throws TruncatedFileException, IOException {
        key = null;
        value = null;
        comment = null;
        isString = false;

        String card = readOneHeaderLine(dis);

        if (FitsFactory.getUseHierarch() && card.length() > 9 && card.substring(0, 9).equals("HIERARCH ")) {
            hierarchCard(card);
            return;
        }

        // We are going to assume that the value has no blanks in
        // it unless it is enclosed in quotes. Also, we assume that
        // a / terminates the string (except inside quotes)

        // treat short lines as special keywords
        if (card.length() < 9) {
            key = card;
            return;
        }

        // extract the key
        key = card.substring(0, 8).trim();

        // if it is an empty key, assume the remainder of the card is a comment
        if (key.length() == 0) {
            key = "";
            comment = card.substring(8);
            return;
        }

        // Non-key/value pair lines are treated as keyed comments
        if (key.equals("COMMENT") || key.equals("HISTORY") || !card.substring(8, 10).equals("= ")) {
            comment = card.substring(8).trim();
            return;
        }

        if (FitsFactory.isLongStringsEnabled() && card.indexOf("&'") >= 0) {
            longStringCard(dis, card);
            return;
        }
        // extract the value/comment part of the string
        String valueAndComment = card.substring(10).trim();

        // If there is no value/comment part, we are done.
        if (valueAndComment.length() == 0) {
            value = "";
            return;
        }

        int vend = -1;
        // If we have a ' then find the matching '.
        if (valueAndComment.charAt(0) == '\'') {

            int offset = 1;
            while (offset < valueAndComment.length()) {

                // look for next single-quote character
                vend = valueAndComment.indexOf("'", offset);

                // if the quote character is the last character on the line...
                if (vend == valueAndComment.length() - 1) {
                    break;
                }

                // if we did not find a matching single-quote...
                if (vend == -1) {
                    // pretend this is a comment card
                    key = null;
                    comment = card;
                    return;
                }

                // if this is not an escaped single-quote, we are done
                if (valueAndComment.charAt(vend + 1) != '\'') {
                    break;
                }

                // skip past escaped single-quote
                offset = vend + 2;
            }

            // break apart character string
            value = valueAndComment.substring(1, vend).trim();
            value = value.replace("''", "'");

            if (vend + 1 >= valueAndComment.length()) {
                comment = null;
            } else {

                comment = valueAndComment.substring(vend + 1).trim();
                if (comment.charAt(0) == '/') {
                    if (comment.length() > 1) {
                        comment = comment.substring(1);
                    } else {
                        comment = "";
                    }
                }

                if (comment.length() == 0) {
                    comment = null;
                }

            }
            isString = true;

        } else {

            // look for a / to terminate the field.
            int slashLoc = valueAndComment.indexOf('/');
            if (slashLoc != -1) {
                comment = valueAndComment.substring(slashLoc + 1).trim();
                value = valueAndComment.substring(0, slashLoc).trim();
            } else {
                value = valueAndComment;
            }
        }
    }

    private void longStringCard(ArrayDataInput dis, String card) throws IOException, TruncatedFileException, EOFException {
        // ok this is a longString now read over all continues.
        StringBuilder longValue = new StringBuilder();
        StringBuilder longComment = new StringBuilder();
        String continueCard = card;
        do {
            int start = continueCard.indexOf('\'') + 1;
            int end = continueCard.indexOf('\'', start);
            while (end > 0 && end < (continueCard.length() - 1) && continueCard.charAt(end + 1) == '\'') {
                end = continueCard.indexOf('\'', end + 1);
            }
            longValue.append(continueCard.substring(start, end));
            int commentStart = continueCard.indexOf('/', end);
            if (commentStart > 0) {
                if (longComment.length() > 0) {
                    longComment.append(' ');
                }
                longComment.append(continueCard.substring(commentStart + 1));
                int commentLength = longComment.length();
                while (commentLength > 0 && Character.isWhitespace(longComment.charAt(commentLength - 1))) {
                    commentLength--;
                }
                longComment.setLength(commentLength);
            }
            if (longValue.charAt(longValue.length() - 1) == '&') {
                longValue.setLength(longValue.length() - 1);
                dis.mark(80);
                continueCard = readOneHeaderLine(dis);
            } else {
                continueCard = null;
            }
        } while (continueCard != null && continueCard.startsWith("CONTINUE"));
        if (continueCard != null) {
            // ok move the imputstream one card back.
            dis.reset();
        }
        this.value = longValue.toString().replace("''", "'");
        this.comment = longComment.toString();
        this.isString = true;
    }

    /**
     * Process HIERARCH style cards... HIERARCH LEV1 LEV2 ... = value / comment
     * The keyword for the card will be "HIERARCH.LEV1.LEV2..." A '/' is assumed
     * to start a comment.
     */
    private void hierarchCard(String card) {

        String name = "";
        String token = null;
        String separator = "";
        int[] tokLimits;
        int posit = 0;
        int commStart = -1;

        // First get the hierarchy levels
        while ((tokLimits = getToken(card, posit)) != null) {
            token = card.substring(tokLimits[0], tokLimits[1]);
            if (!token.equals("=")) {
                name += separator + token;
                separator = ".";
            } else {
                tokLimits = getToken(card, tokLimits[1]);
                if (tokLimits != null) {
                    token = card.substring(tokLimits[0], tokLimits[1]);
                } else {
                    key = name;
                    value = null;
                    comment = null;
                    return;
                }
                break;
            }
            posit = tokLimits[1];
        }
        key = name;

        // At the end?
        if (tokLimits == null) {
            value = null;
            comment = null;
            isString = false;
            return;
        }

        // Really should consolidate the two instances
        // of this test in this class!
        if (token.charAt(0) == '\'') {
            // Find the next undoubled quote...
            isString = true;
            if (token.length() > 1 && token.charAt(1) == '\'' && (token.length() == 2 || token.charAt(2) != '\'')) {
                value = "";
                commStart = tokLimits[0] + 2;
            } else if (card.length() < tokLimits[0] + 2) {
                value = null;
                comment = null;
                isString = false;
                return;
            } else {
                int i;
                for (i = tokLimits[0] + 1; i < card.length(); i += 1) {
                    if (card.charAt(i) == '\'') {
                        if (i == card.length() - 1) {
                            value = card.substring(tokLimits[0] + 1, i);
                            commStart = i + 1;
                            break;
                        } else if (card.charAt(i + 1) == '\'') {
                            // Doubled quotes.
                            i += 1;
                            continue;
                        } else {
                            value = card.substring(tokLimits[0] + 1, i);
                            commStart = i + 1;
                            break;
                        }
                    }
                }
            }
            if (commStart < 0) {
                value = null;
                comment = null;
                isString = false;
                return;
            }
            for (int i = commStart; i < card.length(); i += 1) {
                if (card.charAt(i) == '/') {
                    comment = card.substring(i + 1).trim();
                    break;
                } else if (card.charAt(i) != ' ') {
                    comment = null;
                    break;
                }
            }
        } else {
            isString = false;
            int sl = token.indexOf('/');
            if (sl == 0) {
                value = null;
                comment = card.substring(tokLimits[0] + 1);
            } else if (sl > 0) {
                value = token.substring(0, sl);
                comment = card.substring(tokLimits[0] + sl + 1);
            } else {
                value = token;

                for (int i = tokLimits[1]; i < card.length(); i += 1) {
                    if (card.charAt(i) == '/') {
                        comment = card.substring(i + 1).trim();
                        break;
                    } else if (card.charAt(i) != ' ') {
                        comment = null;
                        break;
                    }
                }
            }
        }
    }

    /**
     * Get the next token. Can't use StringTokenizer since we sometimes need to
     * know the position within the string.
     */
    private int[] getToken(String card, int posit) {

        int i;
        for (i = posit; i < card.length(); i += 1) {
            if (card.charAt(i) != ' ') {
                break;
            }
        }

        if (i >= card.length()) {
            return null;
        }

        if (card.charAt(i) == '=') {
            return new int[]{
                i,
                i + 1
            };
        }

        int j;
        for (j = i + 1; j < card.length(); j += 1) {
            if (card.charAt(j) == ' ' || card.charAt(j) == '=') {
                break;
            }
        }
        return new int[]{
            i,
            j
        };
    }

    /**
     * Does this card contain a string value?
     */
    public boolean isStringValue() {
        return isString;
    }

    /**
     * Is this a key/value card?
     */
    public boolean isKeyValuePair() {
        return key != null && value != null;
    }

    /**
     * Set the key.
     */
    void setKey(String newKey) {
        key = newKey;
    }

    /**
     * Return the keyword from this card
     */
    public String getKey() {
        return key;
    }

    /**
     * Return the value from this card
     */
    public String getValue() {
        return value;
    }

    /**
     * Return the value from this card as a specific type
     */
    public <T> T getValue(Class<T> clazz, T defaultValue) {
        if (String.class.isAssignableFrom(clazz)) {
            return clazz.cast(this.value);
        } else if (this.value == null || this.value.isEmpty()) {
            return defaultValue;
        } else if (Boolean.class.isAssignableFrom(clazz)) {
            if ("T".equals(this.value)) {
                return clazz.cast(Boolean.TRUE);
            } else if ("F".equals(this.value)) {
                return clazz.cast(Boolean.FALSE);
            }
            return clazz.cast(defaultValue);
        }
        BigDecimal parsedValue;
        try {
            parsedValue = new BigDecimal(this.value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
        if (Integer.class.isAssignableFrom(clazz)) {
            return clazz.cast(parsedValue.intValueExact());
        } else if (Long.class.isAssignableFrom(clazz)) {
            return clazz.cast(parsedValue.longValueExact());
        } else if (Double.class.isAssignableFrom(clazz)) {
            return clazz.cast(parsedValue.doubleValue());
        } else if (BigDecimal.class.isAssignableFrom(clazz)) {
            return clazz.cast(parsedValue);
        } else if (BigInteger.class.isAssignableFrom(clazz)) {
            return clazz.cast(parsedValue.toBigIntegerExact());
        } else {
            throw new IllegalArgumentException("unsupported class " + clazz);
        }
    }

    /**
     * Set the value for this card.
     */
    public void setValue(String update) {
        value = update;
    }

    /**
     * Return the comment from this card
     */
    public String getComment() {
        return comment;
    }

    /**
     * Return the 80 character card image
     */
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer(80);

        // start with the keyword, if there is one
        if (key != null) {
            if (key.length() > 9 && key.substring(0, 9).equals("HIERARCH.")) {
                return hierarchToString();
            }
            buf.append(key);
            if (key.length() < 8) {
                buf.append(space80.substring(0, 8 - buf.length()));
            }
        }

        if (value != null || nullable) {
            buf.append("= ");

            if (value != null) {

                if (isString) {
                    String stringValue = value.replace("'", "''");
                    if (FitsFactory.isLongStringsEnabled() && stringValue.length() > HeaderCard.MAX_STRING_VALUE_LENGTH) {
                        // We assume that we've made the test so that
                        // we need to write a long string.
                        // We also need to be careful that single quotes don't
                        // make the string too long and that we don't split
                        // in the middle of a quote.
                        int off = getAdjustedLength(stringValue, 67);
                        String curr = stringValue.substring(0, off) + '&';
                        // No comment here since we're using as much of the card
                        // as we can
                        buf.append('\'');
                        buf.append(stringValue);
                        buf.append('\'');
                        stringValue = stringValue.substring(off);

                        while (stringValue != null && stringValue.length() > 0) {
                            off = getAdjustedLength(stringValue, 67);
                            if (off < stringValue.length()) {
                                curr = "'" + stringValue.substring(0, off).replace("'", "''") + "&'";
                                stringValue = stringValue.substring(off);
                            } else {
                                curr = "'" + stringValue.replace("'", "''") + "' / " + comment;
                                stringValue = null;
                            }
                            buf.append("CONTINUE");
                            buf.append(curr);
                        }

                    } else {
                        // left justify the string inside the quotes
                        buf.append('\'');
                        buf.append(stringValue);
                        if (buf.length() < 19) {

                            buf.append(space80.substring(0, 19 - buf.length()));
                        }
                        buf.append('\'');
                        // Now add space to the comment area starting at column
                        // 40
                        if (buf.length() < 30) {
                            buf.append(space80.substring(0, 30 - buf.length()));
                        }
                    }
                } else {

                    buf.length();
                    if (value.length() < 20) {
                        buf.append(space80.substring(0, 20 - value.length()));
                    }

                    buf.append(value);

                }
            } else {
                // Pad out a null value.
                buf.append(space80.substring(0, 20));
            }

            // if there is a comment, add a comment delimiter
            if (comment != null) {
                buf.append(" / ");
            }

        } else if (comment != null && comment.startsWith("= ")) {
            buf.append("  ");
        }

        // finally, add any comment
        if (comment != null) {
            if (comment.startsWith(" ")) {
                buf.append(comment, 1, comment.length());
            } else {
                buf.append(comment);
            }
        }

        // make sure the final string is exactly 80 characters long
        if (buf.length() > 80) {
            if (isString && FitsFactory.isLongStringsEnabled()) {
                buf.setLength(buf.length() - (buf.length() % 80));
            } else {
                buf.setLength(80);
            }
        } else {

            if (buf.length() < 80) {
                buf.append(space80.substring(0, 80 - buf.length()));
            }
        }

        return buf.toString();
    }

    private int getAdjustedLength(String in, int max) {
        // Find the longest string that we can use when
        // we accommodate needing to double quotes.
        int size = 0;
        int i;
        for (i = 0; i < in.length() && size < max; i += 1) {
            if (in.charAt(i) == '\'') {
                size += 2;
                if (size > max) {
                    break; // Jumped over the edge
                }
            } else {
                size += 1;
            }
        }
        return i;
    }

    private String hierarchToString() {

        StringBuffer b = new StringBuffer(80);
        int p = 0;
        String space = "";
        while (p < key.length()) {
            int q = key.indexOf('.', p);
            if (q < 0) {
                b.append(space + key.substring(p));
                break;
            } else {
                b.append(space + key.substring(p, q));
            }
            space = " ";
            p = q + 1;
        }

        if (value != null || nullable) {
            b.append("= ");

            if (value != null) {
                // Try to align values
                int avail = 80 - (b.length() + value.length());

                if (isString) {
                    avail -= 2;
                }
                if (comment != null) {
                    avail -= 3 + comment.length();
                }

                if (avail > 0 && b.length() < 29) {
                    b.append(space80.substring(0, Math.min(avail, 29 - b.length())));
                }

                if (isString) {
                    b.append('\'');
                } else if (avail > 0 && value.length() < 10) {
                    b.append(space80.substring(0, Math.min(avail, 10 - value.length())));
                }
                b.append(value);
                if (isString) {
                    b.append('\'');
                }
            } else if (b.length() < 30) {

                // Pad out a null value
                b.append(space80.substring(0, 30 - b.length()));
            }
        }

        if (comment != null) {
            b.append(" / " + comment);
        }
        if (b.length() < 80) {
            b.append(space80.substring(0, 80 - b.length()));
        }
        String card = new String(b);
        if (card.length() > 80) {
            card = card.substring(0, 80);
        }
        return card;
    }

    /**
     * @return the type of the value.
     */
    public Class<?> valueType() {
        if (isString) {
            return String.class;
        } else if (value != null) {
            String trimedValue = value.trim();
            if (trimedValue.equals("T") || trimedValue.equals("F")) {
                return Boolean.class;
            } else if (LONG_REGEX.matcher(trimedValue).matches()) {
                int length = trimedValue.length();
                if (trimedValue.charAt(0) == '-' || trimedValue.charAt(0) == '+') {
                    length--;
                }
                if (length <= MAX_INTEGER_STRING_SIZE) {
                    return Integer.class;
                } else if (length <= MAX_LONG_STRING_SIZE) {
                    return Long.class;
                } else {
                    return BigInteger.class;
                }
            } else if (IEEE_REGEX.matcher(trimedValue).find()) {
                // We should detect if we are loosing precision here
                BigDecimal bigDecimal = null;

                try {
                    bigDecimal = new BigDecimal(this.value);
                } catch (Exception e) {
                    throw new NumberFormatException("could not parse " + this.value);
                }
                if (bigDecimal.abs().compareTo(LONG_MAX_VALUE_AS_BIG_DECIMAL) > 0 && bigDecimal.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) == 0) {
                    return BigInteger.class;
                } else if (bigDecimal.doubleValue() == Double.valueOf(trimedValue)) {
                    return Double.class;
                } else {
                    return BigDecimal.class;
                }
            }
        }
        return null;
    }

    /**
     * @return the size of the card in blocks of 80 bytes. So noramlly every
     *         card will return 1. only long stings can return more than one.
     */
    public int cardSize() {
        return 1;
    }
}