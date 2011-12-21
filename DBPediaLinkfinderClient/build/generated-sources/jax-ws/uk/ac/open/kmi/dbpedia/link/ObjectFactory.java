
package uk.ac.open.kmi.dbpedia.link;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the uk.ac.open.kmi.dbpedia.link package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetLinkResponse_QNAME = new QName("http://link.dbpedia.kmi.open.ac.uk/", "getLinkResponse");
    private final static QName _GetLink_QNAME = new QName("http://link.dbpedia.kmi.open.ac.uk/", "getLink");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: uk.ac.open.kmi.dbpedia.link
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetLink }
     * 
     */
    public GetLink createGetLink() {
        return new GetLink();
    }

    /**
     * Create an instance of {@link GetLinkResponse }
     * 
     */
    public GetLinkResponse createGetLinkResponse() {
        return new GetLinkResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetLinkResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://link.dbpedia.kmi.open.ac.uk/", name = "getLinkResponse")
    public JAXBElement<GetLinkResponse> createGetLinkResponse(GetLinkResponse value) {
        return new JAXBElement<GetLinkResponse>(_GetLinkResponse_QNAME, GetLinkResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetLink }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://link.dbpedia.kmi.open.ac.uk/", name = "getLink")
    public JAXBElement<GetLink> createGetLink(GetLink value) {
        return new JAXBElement<GetLink>(_GetLink_QNAME, GetLink.class, null, value);
    }

}
