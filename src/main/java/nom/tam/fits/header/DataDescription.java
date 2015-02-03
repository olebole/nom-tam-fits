package nom.tam.fits.header;

/*
 * #%L
 * nom.tam FITS library
 * %%
 * Copyright (C) 1996 - 2015 nom-tam-fits
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

import nom.tam.fits.header.IFitsHeader.HDU;
import nom.tam.fits.header.IFitsHeader.STATUS;
import nom.tam.fits.header.IFitsHeader.VALUE;

/**
 * File checksum keywords. This data dictionary contains FITS keywords that have
 * been widely used within the astronomical community. It is recommended that
 * these keywords only be used as defined here. These are the keywords that
 * describe the data or the FITS file itself {@link http
 * ://heasarc.gsfc.nasa.gov/docs/fcg/common_dict.html}
 * 
 * @author Richard van Nieuwenhoven
 */
public enum DataDescription implements IFitsHeader {
    /**
     * The value field shall contain a character string giving a title that is
     * suitable for display purposes, e.g., for annotation on images or plots of
     * the data contained in the HDU.
     */
    TITLE(STATUS.ROSAT, HDU.ANY, VALUE.STRING, "title for the observation or data"),
    /**
     * The value field shall contain a character string giving the the host file
     * name used to record the original data.
     */
    FILENAME(STATUS.NOAO, HDU.ANY, VALUE.STRING, "name of the file "),

    /**
     * The value field shall contain a character string giving the file type
     * suffix of the host file name. The full file name typically consists of
     * the root name (see ROOTNAME) followed by a file type suffix, separated by
     * the period ('.') character.
     */
    FILETYPE(STATUS.UNKNOWN, HDU.ANY, VALUE.STRING, "type of file"),

    /**
     * The value field shall contain a character string giving the root of the
     * host file name. The full file name typically consists of the root name
     * followed by a file type suffix (see FILETYPE), separated by the period
     * ('.') character.
     */
    ROOTNAME(STATUS.UNKNOWN, HDU.ANY, VALUE.STRING, "rootname of the file"),

    /**
     * The value field shall contain a character string that uniquely defines
     * the configuration state, or version, of the the software processing
     * system that generated the data contained in the HDU. This keyword differs
     * from the CREATOR keyword in that it give the name and version of the
     * overall processing system and not just the name and version of a single
     * program.
     */
    CONFIGUR(STATUS.INTEGRAL, HDU.ANY, VALUE.STRING, "software configuration used to process the data"),

    /**
     * The value field shall contain an integer giving the number of standard
     * extensions contained in the FITS file. This keyword may only be used in
     * the primary array key.
     */
    NEXTEND(STATUS.STScI, HDU.PRIMARY, VALUE.INTEGER, "Number of standard extensions"),

    /**
     * This keyword is synonymous to the standard EXTNAME keyword except that it
     * may also be used in the primary key. It is recommended that the HDUNAME
     * and EXTNAME keywords should not both be given in the same HDU key, but if
     * they are, then the HDUNAME keyword will have precedence.
     */
    HDUNAME(STATUS.UNKNOWN, HDU.ANY, VALUE.STRING, "descriptive name of the HDU"),

    /**
     * This keyword is synonymous to the standard EXTVER keyword except that it
     * may also be used in the primary key. It is recommended that the HDUVER
     * and EXTVER keywords should not both be given in the same HDU key, but if
     * they are, then the HDUVER keyword will have precedence.
     */
    HDUVER(STATUS.UNKNOWN, HDU.ANY, VALUE.INTEGER, "version number of the HDU"),
    /**
     * This keyword is synonymous to the standard EXTLEVEL keyword except that
     * it may also be used in the primary key. It is recommended that the
     * HDULEVEL and EXTLEVEL keywords should not both be given in the same HDU
     * key, but if they are, then the HDULEVEL keyword will have precedence.
     */
    HDULEVEL(STATUS.UNKNOWN, HDU.ANY, VALUE.INTEGER, "hierarchical level of the HDU"),

    /**
     * The value field of this indexed keyword shall contain a floating point
     * number specifying the lower bound of the legal range of physical values
     * that may be represented in column n of the table. The column may contain
     * values that are less than this legal minimum value but the interpretation
     * of such values is not defined here. The value of this keyword is
     * typically used as the mininum value when constructing a histogram of the
     * values in the column. This keyword may only be used in 'TABLE' or
     * 'BINTABLE' extensions.
     */
    TLMINn(STATUS.HEASARC, HDU.TABLE, VALUE.REAL, "minimum legal value in the column"),
    /**
     * The value field of this indexed keyword shall contain a floating point
     * number specifying the upper bound of the legal range of physical values
     * that may be represented in column n of the table. The column may contain
     * values that are greater than this legal maximum value but the
     * interpretation of such values is not defined here. The value of this
     * keyword is typically used as the maxinum value when constructing a
     * histogram of the values in the column. This keyword may only be used in
     * 'TABLE' or 'BINTABLE' extensions.
     */
    TLMAXn(STATUS.HEASARC, HDU.TABLE, VALUE.REAL, "maximum legal value in the column"),
    /**
     * The value field of this indexed keyword shall contain a floating point
     * number specifying the minimum valid physical value represented in column
     * n of the table, exclusive of any special values. This keyword may only be
     * used in 'TABLE' or 'BINTABLE' extensions and is analogous to the DATAMIN
     * keyword used for FITS images.
     */
    TDMINn(STATUS.HEASARC, HDU.TABLE, VALUE.REAL, "minimum physical value in the column"),
    /**
     * The value field of this indexed keyword shall contain a floating point
     * number specifying the maximum valid physical value represented in column
     * n of the table, exclusive of any special values. This keyword may only be
     * used in 'TABLE' or 'BINTABLE' extensions and is analogous to the DATAMAX
     * keyword used for FITS images.
     */
    TDMAXn(STATUS.HEASARC, HDU.TABLE, VALUE.REAL, "maximum physical value in the column"),
    /**
     * The value field of this indexed keyword shall contain a floating point
     * number specifying the suggested bin size when producing a histogram of
     * the values in column n. This keyword is typically used in conjunction the
     * TLMINn and TLMAXn keywords when constructing a histogram of the values in
     * column n, such that the histogram ranges from TLMINn to TLMAXn with the
     * histogram bin size given by TDBINn. This keyword may only be used in
     * 'TABLE' or 'BINTABLE' extensions.
     */
    TDBINn(STATUS.CXC, HDU.TABLE, VALUE.REAL, "default histogram bin size for the column"),
    /**
     * The value field shall contain a character string that defines the order
     * in which the rows in the current FITS ASCII or binary table extension
     * have been sorted. The character string lists the name (as given by the
     * TTYPEn keyword) of the primary sort column, optionally followed by the
     * names of any secondary sort column(s). The presence of this keyword
     * indicates that the rows in the table have been sorted first by the values
     * in the primary sort column; any rows that have the same value in the
     * primary column have been further sorted by the values in the secondary
     * sort column and so on for all the specified columns. If more than one
     * column is specified by TSORTKEY then the names must be separated by a
     * comma. One or more spaces are also allowed between the comma and the
     * following column name. By default, columns are sorted in ascending order,
     * but a minus sign may precede the column name to indicate that the rows
     * are sorted in descending order. This keyword may only be used in 'TABLE'
     * or 'BINTABLE' extensions. Example: TSORTKEY = 'TIME, RA, DEC'.
     */
    TSORTKEY(STATUS.HEASARC, HDU.TABLE, VALUE.STRING, "defines the sort order of a table"),
    /**
     * The value field shall contain a character string giving the name, and
     * optionally, the version of the program that originally created the
     * current FITS HDU. This keyword is synonymous with the CREATOR keyword.
     * Example: 'TASKNAME V1.2.3'
     */
    PROGRAM(STATUS.UCOLICK, HDU.ANY, VALUE.STRING, "the name of the software task that created the file"),
    /**
     * The value field shall contain a character string giving the name, and
     * optionally, the version of the program that originally created the
     * current FITS HDU. This keyword is synonymous with the PROGRAM keyword.
     * Example: 'TASKNAME V1.2.3'
     */
    CREATOR(STATUS.HEASARC, HDU.ANY, VALUE.STRING, "the name of the software task that created the file"),
    /**
     * The value field shall contain a character string that identifies the
     * domain to which the associated HDUCLASn keywords apply. This keyword
     * typically identifies the institution or project that has defined the
     * allowed set of values for the associated hierarchical HDUCLASn keywords.
     */
    HDUCLASS(STATUS.HEASARC, HDU.ANY, VALUE.STRING, "general identifier for the classification of the data"),
    /**
     * The value field shall contain a character string that gives a reference
     * to a document that describes the allowed values that may be assigned to
     * the HDUCLASn data classification keywords.
     */
    HDUDOC(STATUS.HEASARC, HDU.ANY, VALUE.STRING, "reference to document describing the data format"),
    /**
     * The value field shall contain a character string that gives the specific
     * version of the document referenced by HDUDOC.
     */
    HDUVERS(STATUS.HEASARC, HDU.ANY, VALUE.STRING, "specific version of the document referenced by HDUDOC"),
    /**
     * The value fields of this hierarchical set of indexed keywords shall
     * contain character strings that classify the type of data contained in the
     * HDU. The HDUCLAS1 keyword gives the highest, most general data
     * classification, and the HDUCLAS2 and higher keywords provide
     * progressively more detailed subclassifications of the data.
     */
    HDUCLASn(STATUS.HEASARC, HDU.ANY, VALUE.STRING, "hierarchical classification of the data");

    private IFitsHeader key;

    private DataDescription(IFitsHeader.STATUS status, HDU hdu, VALUE valueType, String comment) {
        this.key = new FitsHeaderImpl(name(), status, hdu, valueType, comment);
    }

    @Override
    public String key() {
        return key.key();
    }

    @Override
    public STATUS status() {
        return key.status();
    }

    @Override
    public HDU hdu() {
        return key.hdu();
    }

    @Override
    public VALUE valueType() {
        return key.valueType();
    }

    @Override
    public String comment() {
        return key.comment();
    }

    @Override
    public IFitsHeader n(int number) {
        return key.n(number);
    }
}
