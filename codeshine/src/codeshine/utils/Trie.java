package codeshine.utils;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

public class Trie {

	/**
	 * @param args
	 */
	Object root;
	Object data;
	List childrens;
	
	public static void main(String[] args) {
		Trie t = new Trie();
		
		t.addToken(new Token("//","single","bla"));
		t.addToken(new Token("/*","multi","bla"));
		
	}
	public Trie(){
		this(null);
	}
	private Trie(Object root){
		this.root = root;
		this.data = null;
		childrens = new ArrayList();
	}
	public int getNumChildren(){
		return this.childrens.size();
	}
	public List getChildrens(){
		return this.childrens;
	}
	public Object getRoot(){
		return this.root;
	}
	public void setRoot(Object root){
		this.root = root;
	}
	public boolean isLeaf(){
		return (childrens.size() == 0);
	}
	public void appendChild(Trie child){
//		System.out.println(this.childrens.size());
		this.childrens.add(child);
	}
	public void removeChild(int index){
		this.childrens.remove(index);
	}
	public void removeAll(){
		this.childrens.clear();
	}
	public void addChild(int index, Trie child){
		this.childrens.add(index, child);
	}
	public boolean addToken(char[] chars, int pos, Object token){
		
		boolean ret = false;
//		Caso base
		if (chars.length == pos){
			this.data = token;
			ret = true;
		}
//		Caso recursivo
		else{
			Character firstChar = new Character(chars[pos]);
			Iterator it = this.childrens.iterator();
			while (it.hasNext()){
				Trie child = (Trie)it.next();
				if (child.getRoot().equals(firstChar)){
					ret = child.addToken(chars, pos+1, token);					
				}
			}
//			El caracter no está -> Añadimos un nuevo subárbol.
			if (!ret){
				Trie child = new Trie(firstChar);
				this.appendChild(child);
//				System.out.println(child);
				ret = child.addToken(chars, pos+1, token);
			}
		}
		return ret;
	}	
			
	public boolean addToken(Object token){
		int pos = 0;
		char[] chars = ((Token)token).getValue().toCharArray();
		return (this.addToken(chars, pos, token));
	}
	
	public boolean isTerminal(){
		return (this.data != null);
	}
	public int search(Object item){
		int pos = -1;
		boolean success = false;
		
		Iterator it = this.childrens.iterator();
		while (it.hasNext() && !success){
			pos++;
			Trie child = (Trie)it.next();
			if (child.getRoot().equals(item)){
				success = true;
			}
		}
		if (!success)
			pos = -1;
		
		return pos;
	}
	public boolean isRoot(){
		return (this.root.equals(null));
	}
	public Trie getChild(int pos){
		return ((Trie)this.childrens.get(pos));
	}
	public String toString(String indent){
		String ret = indent;
		try{
			ret += this.getRoot();
			if (this.isTerminal())
				ret += "<-";
			ret += "\n";
		}catch (NullPointerException e){
			ret += "trie\n";
		}
		Iterator it = this.childrens.iterator();
		while (it.hasNext()){
			ret += ((Trie)it.next()).toString(indent + "  "); 
		}
		return ret;
	}
	

	public Token  trackRecursive(char[] input, int current){
		Token ret = null;
		if (this.isLeaf() && this.isTerminal()){
			ret = (Token)this.data;
		}
		else{
			Iterator it = this.childrens.iterator();
			while (it.hasNext() && (current<input.length)){
				Character ch = new Character(input[current]);
				Trie child = (Trie)it.next();
				if (child.getRoot().equals(ch)){
					ret = child.trackRecursive(input, current+1);
				}
			}
			if (ret == null && this.isTerminal()){
				ret = (Token)this.data;
			}		
		}
		return ret;
	}
}
