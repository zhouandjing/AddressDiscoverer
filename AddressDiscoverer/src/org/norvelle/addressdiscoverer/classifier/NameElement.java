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

import java.util.ArrayList;
import java.util.List;
import org.jsoup.nodes.Element;
import org.norvelle.addressdiscoverer.model.Name;

/**
 *
 * @author Erik Norvelle <erik.norvelle@cyberlogos.co>
 */
public class NameElement {
    
    private final Element nameContainingJsoupElement;
    private final List<Element> containerElements;
    
    public NameElement(Element element) {
        this.nameContainingJsoupElement = element;
        this.containerElements = this.locateContainerElements();
    }
    
    public ContactLink getContactLink() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public Name getName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<Element> getContainerElements() {
        return this.containerElements;
    }

    /**
     * Given an element, find the TR, UL, OL or P or DIV that most immediately contains it.
     * 
     * @return 
     */
    private List<Element> locateContainerElements() {
        List<Element> myContainerElements = new ArrayList<>();
        
        // First, see if we can find a TR... giving TRs priority over Ps and other containers
        Element currElement = nameContainingJsoupElement.parent();
        Element trContainer = null;
        while (currElement != null) {
            if (currElement.tagName().equals("tr")) {
                trContainer = currElement;
                break;
            }
            currElement = currElement.parent();
        }
        
        // Next we check for a P, UL, OL or DIV that contains the current element
        Element otherContainer = null;
        if (nameContainingJsoupElement.tagName().equals("p") 
                || nameContainingJsoupElement.tagName().equals("div") 
                || nameContainingJsoupElement.tagName().equals("ul")
                || nameContainingJsoupElement.tagName().equals("ol")
           ) 
        {
            otherContainer = nameContainingJsoupElement;
        }
        currElement = nameContainingJsoupElement.parent();
        if (otherContainer == null)
            while (currElement != null) {
                if (currElement.tagName().equals("p") || currElement.tagName().equals("div") 
                    || currElement.tagName().equals("ul")
                    || currElement.tagName().equals("ol"))
                {
                    otherContainer = currElement;
                    break;
                }
                currElement = currElement.parent();
            }
        
        // Now, return the element that best fits the criterion of being the parent
        if (trContainer == null && otherContainer == null)
            return myContainerElements;
        if (trContainer != null)
            myContainerElements.add(trContainer);
        if (otherContainer != null)
            myContainerElements.add(otherContainer);
        
        return myContainerElements;         
    }

    @Override
    public String toString() {
        return this.nameContainingJsoupElement.ownText();
    }
    
}
