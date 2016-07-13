package com.deleidos.framework.operators.csv.parser;

import java.text.ParseException;
import java.util.ArrayList;

/**
 * Utility class used to parse comma-separated-values data.
 */   
public final class CSVParser {
    
    /**
     * Constructor.
     */
    private CSVParser() {
        super();
    }
    
    /**
     * Splits a CSV record into fields and assigns them to the given buffer.
     *   
     * @param record Data to be parsed.
     * @param offset Index populate buffer from.
     * @param delimiter Field delimiter.
     * 
     * @return Number of fields found.
     * 
     * @throws ParseException Could throw a ParseException
     */
    public static String[] parse(CharSequence record, int offset, char delimiter) throws ParseException {

    	ArrayList <CharSequence> buffer = new ArrayList<CharSequence>();
        int fieldStart = 0;
        int fieldEnd = 0;
        char c = 0;
        int i = 0;
        
        for (int j=1; j<=offset; j++) {
			buffer.add(null);
		}
        
        while (i < record.length()) {
            
            // Skip an leading white space.
            i += countLeadingSpace(record, i, delimiter);
            if (i < record.length()) {
                c = record.charAt(i);
            } else {
                break;
            }
            
            // Empty field.
            if (c == delimiter) {
                buffer.add("");
                
            // Quoted field.
            } else if (c == '"') {
                fieldStart = i + 1;
                i += countQuotedField(record, i, delimiter);
                fieldEnd = i - countTrailingSpace(record, i, delimiter) - 1;
                buffer.add(clean(record.subSequence(fieldStart, fieldEnd)));
                
            // Normal field.
            } else {
                fieldStart = i;
                i += countField(record, i, delimiter);
                fieldEnd = i - countTrailingSpace(record, i, delimiter);
                buffer.add(record.subSequence(fieldStart, fieldEnd));
            }
            i++;
            
        }
        
        // If the last field is empty, populate it.
        if (i == record.length()) {
            buffer.add("");
        }
        
        return buffer.toArray(new String [buffer.size()]);
        
    }
    
    /**
     * Counts the number of consective space charactes that occur
     * between 'start' and the first non-space character.
     */
    private static int countLeadingSpace(CharSequence record, int start, char delimiter) {
        int i = start;
        while (i < record.length()) {
            char c = record.charAt(i);
            if (c == delimiter) {  // for table delimited files, a tab is not treated as a space
                break;
            } else if (c != ' ' && c != '\t') {
                break;
            } else {
                i++;
            }
        }
        return i - start;
    }

    /**
     * Counts the number of consective space charactes that occur
     * ahead of 'start' and past the last non-space character.
     */
    private static int countTrailingSpace(CharSequence record, int start, char delimiter) {
        int i = start - 1;
        while (i >= 0) {
            char c = record.charAt(i);
            if (c == delimiter) { // for table delimited files, a tab is not treated as a space
                break;
            } else if (c != ' ' && c != '\t') {
                break;
            } else {
                i--;
            }
        }
        return start - i - 1;
    }
    
    /**
     * Counts the number of characters between 'start' and the next delimiter.
     */
    private static int countField(CharSequence record, int start, char delimiter) throws ParseException {
        int i = start;
        while (i < record.length()) {
            char c = record.charAt(i);
            if (c == delimiter) {
                break;
            } else {
                i++;
            }
        }
        return i - start;
    }

    /**
     * Counts the number of character between 'start' and the next delimiter. 
     */
    private static int countQuotedField(CharSequence record, int start, char delimiter) throws ParseException {
        
        int i = start + 1;
        
        // Loop through the quoted characters.
        while (true) {
            // If terminating qoute is missing, barf...
            if (i == record.length()) {
                throw new ParseException("No terminating quote found.", i);
            }
            char c = record.charAt(i);
            // If this char is a quote...
            if (c == '"') {
                i++;
                // If nothing follows, it's the end of the field.
                if (i == record.length()) {
                    break;
                // If another quote follows, it's an escaped quote.
                } else if (record.charAt(i) == '"') {
                    i++;
                // If anything else follow, it's the end of the field.
                } else {
                    break;
                }
            } else {
                i++;
            }
        }
        
        // Loop through any space that follows.
        while (i < record.length()) {
            char c = record.charAt(i);
            // If this char is the delimiter, it's the end of the field.
            if (c == delimiter) {
                break;
            // If it's space, keep going.
            } else if (c == ' ' || c == '\t') {
                i++;
            // Anything else is not valid CSV.
            } else {
                throw new ParseException("Non-space character found outside of quoted string.", i);
            }
        }
        
        return i - start;
        
    }
    
    /**
     * Replaces all escaped quotes with actual quotes.  
     * @param field Field
     * @return String
     */
    private static String clean(final CharSequence field) {
        return field.toString().replaceAll("\"\"", "\"");
    }
}
