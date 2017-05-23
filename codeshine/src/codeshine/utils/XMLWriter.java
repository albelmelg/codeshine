package codeshine.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;


public class XMLWriter {

	private BufferedWriter out;
	private Enumeration elements; 
	
	
	public XMLWriter(Enumeration content, String filePath) throws IOException{
		this.elements = content;
		try{
			out = new BufferedWriter(new FileWriter(filePath));
		}catch (IOException ioexception){throw (ioexception);}
	}
	
	public void writeFile(){
		try{
			this.startDocument();
		}catch (IOException ioexception){ioexception.printStackTrace();}
		while (elements.hasMoreElements()){
			Token t = null;
			try{
				t = (Token)elements.nextElement();}
			catch(ClassCastException e){e.printStackTrace();}
			try{
				emit("\t\t<token");
				emit("\t\t\tvalue=\"" + (t.getValue() + "\""));
				emit("\t\t\treplaced=\"" + (t.getReplacement() + "\""));
				emit("\t\t\tinfo=\"" + (t.getInfo() + "\">"));
				emit("\t\t</token>");
			}catch (IOException e) {
				e.printStackTrace();
			}
		}
		try{
			this.endDocument();
		}catch (IOException e){e.printStackTrace();}
	}
	private void startDocument()
    throws IOException
    {
        emit("<?xml version='1.0' encoding='UTF-8'?>");
        emit("<" + "profile" + "\n" + "\ttitle = \"CodeShine Profile\"");
        emit("\tauthor=\"CodeShine Plugin\">");
      
    }
	private void endDocument()
    throws IOException
    {
        try {
            nl();
            out.flush();
        } catch (IOException e) {
            throw new IOException("I/O error");
        }
		emit("</profile>");
    }
	private void emit(String s) throws IOException
    {
        
		try {
            out.write(s);
            out.flush();
        } catch (IOException e) {
            throw new IOException("I/O Error");
        }
//		System.out.print(s);
		nl();
    }

    // Start a new line
    private void nl()
    throws IOException
    {
        String lineEnd =  System.getProperty("line.separator");
//        System.out.print(lineEnd);
        
        try {
            out.write(lineEnd);
        } catch (IOException e) {
            throw new IOException("I/O Error");
        }
    }

}
