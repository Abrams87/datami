
package uk.ac.open.kmi.dbpedia.link;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for getLink complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getLink">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="queryURL" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getLink", propOrder = {
    "queryURL"
})
public class GetLink {

    protected String queryURL;

    /**
     * Gets the value of the queryURL property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQueryURL() {
        return queryURL;
    }

    /**
     * Sets the value of the queryURL property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQueryURL(String value) {
        this.queryURL = value;
    }

}
