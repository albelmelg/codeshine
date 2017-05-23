package codeshine.utils;


import codeshine.speech.TtsClass;

public class TestTrie {

	/**
	 * @param args
	 */
	static int tag = 0;
	static int initPos = 0;
	static int currPos = 0;
	static Token token = null;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Trie t = new Trie();
		t.addToken(new Token("sioque","no","bla"));
		t.addToken(new Token("pono","si","bla"));
		t.addToken(new Token("/**","javadoc","quemachote"));
		t.addToken(new Token("/*","multilinea","quemachote"));
		t.addToken(new Token("//","una linea","quemachote"));
		System.out.println(t.toString(" "));
//		String prueba = trackString("/**: primero /*: segundo //: tercero", t);
		String prueba2 = trackString("///**/*", t);
		System.out.println("Orig: ///**/*");
		System.out.println("Resul:" + prueba2);
//		TtsClass t = new TtsClass();
	
		
		// TODO Auto-generated method stub
	}
	public static String trackString(String input, Trie trie){
			
			String ret="";
			char[] chars = input.toCharArray();
			Token t = null;
			while (currPos < input.length()){
				t = trie.trackRecursive(chars, currPos);
				if (t != null){
					ret += t.getReplacement();
					currPos += t.getValue().length();
					System.out.println("Reemplazo ->" + t.getReplacement());
				}
				else if (currPos<input.length()){
					ret += chars[currPos];
					currPos++;
				}
			}
			return ret;			
			}
}
