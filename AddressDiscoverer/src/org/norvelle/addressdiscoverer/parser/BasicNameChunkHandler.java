/**
 * Part of the AddressDiscoverer project, licensed under the GPL v.3 license.
 * This project provides intelligence for discovering email addresses in
 * specified web pages, associating them with a given institution and department
 * and address type.
 *
 * This project is licensed under the GPL v.3. Your rights to copy and modify
 * are regulated by the conditions specified in that license, available at
 * http://www.gnu.org/licenses/gpl-3.0.html
 */
package org.norvelle.addressdiscoverer.parser;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.norvelle.addressdiscoverer.exceptions.CantParseIndividualException;
import org.norvelle.addressdiscoverer.exceptions.OrmObjectNotConfiguredException;
import org.norvelle.addressdiscoverer.model.Name;

/**
 * Handles a chunk of text thought to have a first and last name(s)
 * 
 * @author Erik Norvelle <erik.norvelle@cyberlogos.co>
 */
public class BasicNameChunkHandler {
    
    // A logger instance
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME); 

    private static final String hasCommaRegex = "^(.*),(.*)$";
    private static Pattern hasCommaPattern;

    private String firstName;
    private String lastName;
    private String fullName;
    private String rest;
    
    public BasicNameChunkHandler() {}
    
    /**
     * From a chunk of text representing an HTML table row, find, if possible,
     * the first and last names of the individual, plus his email and other info.
     *
     * @param chunk A String containing HTML for a table row
     * @see getFirstName(), etc. for methods to retrieve the results, if any
     * @throws SQLException
     * @throws OrmObjectNotConfiguredException 
     */
    public Name processChunkForName(String chunk) 
            throws SQLException, OrmObjectNotConfiguredException, CantParseIndividualException
    {
        logger.log(Level.INFO, "Processing chunk for name: {0}", chunk);

        if (BasicNameChunkHandler.hasCommaPattern == null)
            BasicNameChunkHandler.hasCommaPattern = Pattern.compile(BasicNameChunkHandler.hasCommaRegex);
        
        // Eliminate non-breaking spaces
        chunk = chunk.replaceAll("\\xA0", " ");
        
        // Eliminate numbers... no use for them.
        chunk = chunk.replaceAll("\\b\\d+\\b", "").trim();
        
        // First see if we have a name divided by a comma
        Name name;
        Matcher hasCommaMatcher = BasicNameChunkHandler.hasCommaPattern.matcher(chunk);
        if (hasCommaMatcher.matches()) 
            name = new Name(hasCommaMatcher.group(2).trim(), hasCommaMatcher.group(1).trim());
        
        // Otherwise, it's all one long string, so we do our best at splitting 
        // it into first and last names. Our assumption will be that 
        else 
            name = new Name(chunk);
        
        // If we were unable to get a first name, consider this a failed parse
        if (name.getScore() == 0.0)
            throw new CantParseIndividualException(chunk);
        
        return name;
    }
    
}
