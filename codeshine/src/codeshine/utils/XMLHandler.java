package codeshine.utils;

import java.io.Writer;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class XMLHandler extends DefaultHandler {

	static public Writer out;
	SAXParserFactory factory;
	private TokenList tl;
	/**
	 * Constructor
	 * @param tl la TokenList
	 */
	public XMLHandler(TokenList tl){
		
        this.tl = tl;
		
	}
	/**
	 * Recibe una notificacion de que un elemento se está ejecutando.
	 * Por defecto no hace nada.
	 * 
	 * @param namespaceURI El Namespace URI, o un String vacio si no hay un namespaceURI
	 * @param lName Nombre local (sin prefijo) o un String vacio si no se ha formado
	 * @param qName Nombre 'qualified' (con prefijo) o un Strin vacio si no se ha formado
	 * @param attrs Los atributos unidos al elemento. Si no hay atributos sera un objeto vacio
	 * @see ContentHandler.startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 * @throws SAXException
	 */

    public void startElement(String namespaceURI,
                             String lName, // local name
                             String qName, // qualified name
                             Attributes attrs)
    throws SAXException
    {
        String eName = lName; // element name
        
        Token t = new Token();
        if ("".equals(eName)) eName = qName; // namespaceAware = false
        if (attrs != null && qName.equals("token")) {
            for (int i = 0; i < attrs.getLength(); i++) {
                String aName = attrs.getLocalName(i); // Attr name
                
                if (attrs.getQName(i).equals("value"))
                	t.setValue(attrs.getValue(i));
                else if (attrs.getQName(i).equals("replaced"))
					t.setReplacement(attrs.getValue(i));
                else if (attrs.getQName(i).equals("info"))
                	t.setInfo(attrs.getValue(i));
                else
                	System.out.println("Formato incorrecto");
                if ("".equals(aName)) aName = attrs.getQName(i);

            }
            tl.addToken(t);
        }

    }

}
