
package uk.ac.open.kmi.dbpedia.link;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;
import javax.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2-hudson-740-
 * Generated source version: 2.1
 * 
 */
@WebService(name = "linkfinderWS", targetNamespace = "http://link.dbpedia.kmi.open.ac.uk/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface LinkfinderWS {


    /**
     * 
     * @param queryURL
     * @return
     *     returns java.lang.String
     */
    @WebMethod
    @WebResult(targetNamespace = "")
    @RequestWrapper(localName = "getLink", targetNamespace = "http://link.dbpedia.kmi.open.ac.uk/", className = "uk.ac.open.kmi.dbpedia.link.GetLink")
    @ResponseWrapper(localName = "getLinkResponse", targetNamespace = "http://link.dbpedia.kmi.open.ac.uk/", className = "uk.ac.open.kmi.dbpedia.link.GetLinkResponse")
    public String getLink(
        @WebParam(name = "queryURL", targetNamespace = "")
        String queryURL);

}