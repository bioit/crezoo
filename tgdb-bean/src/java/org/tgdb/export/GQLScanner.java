package org.tgdb.export;

import java.util.*;
import java.io.*;


/**
 * Legacy class for parsing a GQL expression
 */
public class GQLScanner {
    private String m_gqlString = null;
    private String m_workingstring = null;
    private String m_nextToken = null;
    
    // Default constructor
    /**
     * Constructs a new GQLScanner object
     */
    public GQLScanner() {
        m_nextToken = null;
    }
    
    // Constructs the class with working string set to sNewString
    /**
     * Creates a new instance of GQLScanner
     * @param sNewString The string to parse
     */
    public GQLScanner(String sNewString) {
        m_gqlString = new String(sNewString);
        // The trim() at the end of the row below is crucial in
        // setNextToken(). Without it we might run into an
        // ArrayOutOfBoundsException.
        m_workingstring = m_gqlString.toString().trim();
        setNextToken();
    }
    
    // Sets the working string
    /**
     * Sets the GQL string to parse
     * @param sNewString The string to parse
     */
    public void setQGLString(String sNewString) {
        m_gqlString = new String(sNewString);
        m_workingstring = m_gqlString.toString();
        setNextToken();
    }
    
    // Returns the working string
    /**
     * Returns the GQL string
     * @return The GQL string
     */
    public String getGQLString() {
        return m_gqlString.toString();
    }
    
    // Returns the next token if any, otherwise null
    /**
     * Fetches the next token in the GQL string
     * @return The next token in the GQL string
     */
    public String getNextToken() {
        String temp = m_nextToken.toString();
        setNextToken();
        return temp;
    }
    
    /**
     * Returns wether or not the GQL string has more tokens
     * @return True if more tokens, false otherwise
     */
    public boolean hasMoreTokens() {
        if (m_nextToken != null)
            return true;
        else
            return false;
    }
    
    private void setNextToken() {
        // Check if there are no more tokens
        if (m_workingstring == null || m_workingstring.length() == 0) {
            m_workingstring = null;
            m_nextToken = null;
            return;
        }
        
        // skip white spaces in the beginning of tokens
        int i = 0;
        while(m_workingstring.charAt(i) == ' ')
            i++;
        m_workingstring = m_workingstring.substring(i);
        i = 0;
        // First we check for single char tokens
        char ch = m_workingstring.charAt(i);
        if (ch == '(' || ch == ')' ||
                ch == '=' || ch == ' ' ||
                ch == '<' || ch == '>' ||
                ch == '\n' || ch == '\r' ||
                ch == '\t' || ch == ',') {
            m_nextToken = m_workingstring.substring(0, 1);
            m_workingstring = m_workingstring.substring(1);
        } else if (ch == '!') {
            if(m_workingstring.charAt(i+1) == '=') {
                m_nextToken = m_workingstring.substring(0, 2);
                m_workingstring = m_workingstring.substring(2);
            } else {
                m_nextToken = m_workingstring.substring(0, 1);
                m_workingstring = m_workingstring.substring(1);
            }
        } else {
            // This token is composed by more than one char
            i++;
            boolean complete = false;
            while(!complete && i < m_workingstring.length()) {
                ch = m_workingstring.charAt(i);
                if (ch == '(' || ch == ')' || ch == '=' ||
                        ch == '!' || ch == ' ' ||
                        ch == '<' || ch == '>' ||
                        ch == '\n' || ch == '\r' ||
                        ch == '\t' || ch == ',') {
                    complete = true;
                    continue;
                }
                i++;
            }
            m_nextToken = m_workingstring.substring(0, i);
            m_workingstring = m_workingstring.substring(i);
        }
    }
}