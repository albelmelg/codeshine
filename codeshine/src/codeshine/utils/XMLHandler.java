package codeshine.utils;

import java.io.*;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import codeshine.utils.Token;


public class XMLHandler extends DefaultHandler {

	static public Writer out;
	SAXParserFactory factory;
	private TokenList tl;
		
	public XMLHandler(TokenList tl){
		
        this.tl = tl;
		
	}
	public void startDocument()
    throws SAXException
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
        
//        System.out.println("ELEMENT:" + qName);
//        System.out.println("LENGTH: " + attrs.getLength());
//        System.out.println("ATRIB 0: " + attrs.getQName(0));
        Token t = new Token();
        if ("".equals(eName)) eName = qName; // namespaceAware = false
//        emit("<"+eName);
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
//                emit(" ");
//                emit(aName+"=\""+attrs.getValue(i)+"\"");
            }
            tl.addToken(t);
//            System.out.println("new token added: #" + (tl.length()-1));
        }
//        emit(">");
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

    // Start a new line
    private void nl()
    throws SAXException
    {
        
    }

}
