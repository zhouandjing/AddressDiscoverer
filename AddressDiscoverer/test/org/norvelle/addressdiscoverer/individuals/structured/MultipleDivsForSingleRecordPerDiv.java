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
package org.norvelle.addressdiscoverer.individuals.structured;

import com.j256.ormlite.support.ConnectionSource;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;
import junit.framework.Assert;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.norvelle.addressdiscoverer.TestUtilities;
import org.norvelle.addressdiscoverer.gui.threading.ExtractIndividualsStatusReporter;
import org.norvelle.addressdiscoverer.classifier.IProgressConsumer;
import org.norvelle.addressdiscoverer.exceptions.CannotLoadJDBCDriverException;
import org.norvelle.addressdiscoverer.exceptions.DoesNotContainContactLinkException;
import org.norvelle.addressdiscoverer.exceptions.EndNodeWalkingException;
import org.norvelle.addressdiscoverer.exceptions.MultipleContactLinksOfSameTypeFoundException;
import org.norvelle.addressdiscoverer.model.Department;
import org.norvelle.addressdiscoverer.model.Individual;
import org.norvelle.addressdiscoverer.model.Institution;
import org.norvelle.addressdiscoverer.model.Name;
import org.norvelle.addressdiscoverer.parse.INameElement;
import org.norvelle.addressdiscoverer.parse.ContactLink;
import org.norvelle.addressdiscoverer.parse.structured.StructuredPageNameElement;
import org.norvelle.addressdiscoverer.parse.INameElementFinder;
import org.norvelle.addressdiscoverer.parse.structured.StructuredNameElementFinder;
import org.norvelle.utils.Utils;

/**
 *
 * @author Erik Norvelle <erik.norvelle@cyberlogos.co>
 */
public class MultipleDivsForSingleRecordPerDiv implements IProgressConsumer {
    
    // A logger instance
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME); 
    private static ConnectionSource connection;
    private static Document soup;
    private ExtractIndividualsStatusReporter status;
    private INameElementFinder nameElementFinder;
    private static Department department;

    public MultipleDivsForSingleRecordPerDiv() {
    }

    @Override
    public void reportProgressStage(ExtractIndividualsStatusReporter progress) {
        //System.out.println(progress.toString());
    }
    
    @Override
    public void reportText(String text) {
        //System.out.println(text);
    }

    @BeforeClass
    @SuppressWarnings("UnnecessaryReturnStatement")
    public static void setUpClass() {
        TestUtilities.setupLogger();
        try {
            // Set up a fresh database with dummy objects
            TestUtilities.deleteDatabase("addresses.test.sqlite");
            connection = TestUtilities.getDBConnection("addresses.test.sqlite");
            Institution i = Institution.create("Dummy institution");
            department = Department.create("Dummy department", i);
            
            // Fetch the page we'll be working on
            String htmlUri = "/org/norvelle/addressdiscoverer/resources/individuals/MultipleDivsForSingleRecordPerDiv.html";
            String html = Utils.loadStringFromResource(htmlUri, "UTF-8");
            soup = Jsoup.parse(html);
        } catch (SQLException | CannotLoadJDBCDriverException |IOException ex) {
            fail("Encountered problems connecting to database: " + ex.getMessage());
            return;
        }
    }

    @Before
    public void setUp() {
        try {
            status = new ExtractIndividualsStatusReporter(
                    ExtractIndividualsStatusReporter.ClassificationStages.CREATING_ITERATOR, this);
            nameElementFinder = new StructuredNameElementFinder(soup, "UTF-8", status);
        } catch (UnsupportedEncodingException | EndNodeWalkingException ex) {
            fail("Encountered problems reading file: " + ex.getMessage());
        }
    }    
    
    @Test
    public void testGetNameElement() {
        try {            
            // Check for correct number of contact links found
            Assert.assertEquals("Should find one name element", 1, nameElementFinder.getNameElements().size());
            
            // Check we have the correct name found
            List<INameElement> nameElements = nameElementFinder.getNameElements();
            INameElement adeval = nameElements.get(0);
            Assert.assertEquals("Name should be GONZALO FISAC, JESUS", 
                    "GONZALO FISAC, JESUS", adeval.toString());
            Name name = adeval.getName();
            Assert.assertEquals("First name should be Jesus", "Jesus", name.getFirstName());
            Assert.assertEquals("Last name should be Gonzalo Fisac", "Gonzalo Fisac", name.getLastName());
        } catch (Exception ex) {
            fail("Encountered problems reading file: " + ex.getMessage());
        }
    }
    
    @Test
    public void testGetContactLink() {
        try {                        
            // Check we have the correct name found
            List<INameElement> nameElements = nameElementFinder.getNameElements();
            INameElement adeval = nameElements.get(0);
            ContactLink cl = adeval.getContactLink();
            Assert.assertEquals("Email address must be jesus.gonzalez@uca.es", "jesus.gonzalez@uca.es", cl.getAddress());
        } catch (MultipleContactLinksOfSameTypeFoundException ex) {
            fail("Found too many contact links");
        } catch (DoesNotContainContactLinkException ex) {
            fail("No contact links found");
        }
    }
     
    @Test
    public void testCreateIndividual() {
        try {                        
            // Check we have the correct name found
            List<INameElement> nameElements = nameElementFinder.getNameElements();
            INameElement nm = nameElements.get(0);
            ContactLink cl = nm.getContactLink();
            Name name = nm.getName();
            Individual i = new Individual(name, cl.getAddress(), "", department);
            Individual.store(i);
            Assert.assertEquals("There should be one individual in db", 1, Individual.getCount());
        } catch (Exception ex) {
            fail("Problem: " + ex.getMessage());
        }
    }
     
}
