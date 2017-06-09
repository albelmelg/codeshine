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
		
	public XMLHandler(TokenList tl){
		
        this.tl = tl;
		
	}
	public void startDocument() throws SAXException
    {
      
    }

    public void endDocument()
    throws SAXException
    {
      
    }

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
