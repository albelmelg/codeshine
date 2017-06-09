package codeshine.utils;

import java.io.IOException;
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

    public void endElement(String namespaceURI,
                           String sName, // simple name
                           String qName  // qualified name
                          )
    throws SAXException
    {
        
    }

    public void characters(char buf[], int offset, int len)
    throws SAXException
    {
        
    }

    //===========================================================
    // Utility Methods ...
    //===========================================================

    // Wrap I/O exceptions in SAX exceptions, to
    // suit handler signature requirements
    private void emit(String s)
    throws SAXException
    {
        try {
            out.write(s);
            out.flush();
        } catch (IOException e) {
            throw new SAXException("I/O error", e);
        }
    }

}
