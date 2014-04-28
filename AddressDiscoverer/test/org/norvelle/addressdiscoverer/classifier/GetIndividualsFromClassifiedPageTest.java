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
package org.norvelle.addressdiscoverer.classifier;

import com.j256.ormlite.support.ConnectionSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.norvelle.addressdiscoverer.TestUtilities;
import org.norvelle.addressdiscoverer.classifier.PageClassifier.Classification;
import org.norvelle.addressdiscoverer.exceptions.CannotLoadJDBCDriverException;
import org.norvelle.addressdiscoverer.exceptions.EndNodeWalkingException;
import org.norvelle.utils.Utils;

/**
 *
 * @author Erik Norvelle <erik.norvelle@cyberlogos.co>
 */
public class GetIndividualsFromClassifiedPageTest implements IProgressConsumer {
    
    // A logger instance
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME); 
    private static ConnectionSource connection;
    
    private NameElementFinder nameElementFinder;

    public GetIndividualsFromClassifiedPageTest() {
    }

    @Override
    public void reportProgressStage(ClassificationStatusReporter progress) {
        System.out.println(progress.toString());
    }
    
    @Override
    public void reportText(String text) {
        System.out.println(text);
    }

    @BeforeClass
    @SuppressWarnings("UnnecessaryReturnStatement")
    public static void setUpClass() {
        TestUtilities.setupLogger();
        try {
            connection = TestUtilities.getDBConnection("addresses.test.sqlite");
        } catch (SQLException | CannotLoadJDBCDriverException |IOException ex) {
            fail("Encountered problems connecting to database: " + ex.getMessage());
            return;
        }
    }

    @Before
    public void setUp() {
        try {
            String htmlUri = "/org/norvelle/addressdiscoverer/resources/MultipleTdsWithRecordsPerTr.html";
            String html = Utils.loadStringFromResource(htmlUri, "iso-8859-1");
            Document soup = Jsoup.parse(html);

            // Classify the page to discover its structure
            ClassificationStatusReporter status = new ClassificationStatusReporter(
                ClassificationStatusReporter.ClassificationStages.CREATING_ITERATOR, this);
            nameElementFinder = 
                new NameElementFinder(soup, "iso-8859-1", status);
            ContactLinkFinder clFinder = new ContactLinkFinder(nameElementFinder, soup, status);
            Assert.assertEquals("The page should have associated contact info", 
                    ContactLinkFinder.PageContactType.HAS_ASSOCIATED_CONTACT_INFO, clFinder.getPageContactType());

            PageClassifier classifier = new PageClassifier(nameElementFinder, clFinder, status);
            PageClassifier.Classification classification = classifier.getClassification();
            Assert.assertEquals("The classification should be TR structured page", 
                    Classification.STRUCTURED, classification);            
        } catch (IOException | EndNodeWalkingException ex) {
            fail("Encountered problems reading file: " + ex.getMessage());
        }
    }

    @Test
    public void testMultipleTdsWithRecordsPerTr() {
        try {
            for (NameElement ne : nameElementFinder.getNameElements()) {
                ContactLink cl = ne.getContactLink();
                Assert.assertNotNull("Contact link should not be null for " + ne.toString(), cl);
                Assert.assertEquals("Contact link should be to a detail page", 
                        ContactLink.ContactType.LINK_TO_DETAIL_PAGE, cl.getType());
                if (cl.getType() == ContactLink.ContactType.LINK_TO_DETAIL_PAGE) 
                    cl.fetchEmailFromWeblink();                  
            }           
            
        } catch (Exception ex) {
            fail("Encountered problems reading file: " + ex.getMessage());
        }
    }
    
}
