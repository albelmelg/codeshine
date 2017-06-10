package codeshine.utils;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
/**
 * Clase que define los objetos Trie y los metodos que pueden usar.
 *
 */
public class Trie {

	Object root;
	Object data;
	List<Trie> childrens;
	/**
	 * Metodo que al ejecutarse crea objetos de tipo Trie y los añade a una lista
	 * @param args Los argumentos que recibe el metodo
	 */
	public static void main(String[] args) {
		Trie t = new Trie();
		
		t.addToken(new Token("//","single","bla"));
		t.addToken(new Token("/*","multi","bla"));
		
	}
	/**
	 * Constructor
	 */
	public Trie(){
		this(null);
	}
	/**
	 * Constructor, inicializa el ArrayList
	 * @param root Objeto que se le pasa
	 */
	private Trie(Object root){
		this.root = root;
		this.data = null;
		childrens = new ArrayList<Trie>();
	}
	/**
	 * Informa del numero de elementos que hay en la ArrayList
	 * @return El numero de elementos del ArrayList
	 */
	public int getNumChildren(){
		return this.childrens.size();
	}
	/**
	 * Devuelve una lista de objetos Trie
	 * @return Una lista de objetos Trie
	 */
	public List<Trie> getChildrens(){
		return this.childrens;
	}
	/**
	 * Devuelve el objeto root, el principal de la lista
	 * @return El objeto root
	 */
	public Object getRoot(){
		return this.root;
	}
	/**
	 * Establece un valor para root
	 * @param root El objeto a modificar
	 */
	public void setRoot(Object root){
		this.root = root;
	}
	/**
	 * Indica si la lista está vacía
	 * @return True si está vacía o False en el caso contrario
	 */
	public boolean isLeaf(){
		return childrens.size() == 0;
	}
	/**
	 * Añade 'hijos' (elementos de tipo Trie) a la lista
	 * @param child El objeto que se quiere añadir
	 */
	public void appendChild(Trie child){
		this.childrens.add(child);
	}
	/**
	 * Elimina 'hijos' a la lista
	 * @param index el indice en la lista que se quiere eliminar
	 */
	public void removeChild(int index){
		this.childrens.remove(index);
	}
	/**
	 * Elimina todos los objetos de la lista
	 */
	public void removeAll(){
		this.childrens.clear();
	}
	/**
	 * Añade un elemento en el indice que se le pasa.
	 * En el caso de que ahi hubiera un elemento lo mueve a la derecha sumando 1 a su indice.
	 * @param index El indice donde se quiere añadir el objeto
	 * @param child El objeto a añadir
	 */
	public void addChild(int index, Trie child){
		this.childrens.add(index, child);
	}
	/**
	 * Metodo recursivo que añade el objeto token en el parametro data.
	 * El metodo solo añade el objeto cuando llega al final de la lista.
	 * @param chars Array de objetos char 
	 * @param pos La posicion
	 * @param token El objeto que se añade
	 * @return Devuelve True si añade al objeto y False en caso contrario
	 */
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
			final Iterator<Trie> it = this.childrens.iterator();
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
				ret = child.addToken(chars, pos+1, token);
			}
		}
		return ret;
	}	
	/**
	 * Añade el valor del objeto convertido de String a Char en un array de chars	
	 * @param token El objeto a añadir
	 * @return True si lo añade y False en caso contrario
	 */
	public boolean addToken(Object token){
		int pos = 0;
		char[] chars = ((Token)token).getValue().toCharArray();
		return (this.addToken(chars, pos, token));
	}
	/**
	 * Indica si un objeto es el ultimo de la lista
	 * @return True si lo es, False si no.
	 */
	public boolean isTerminal(){
		return this.data != null;
	}
	/**
	 * Busca el objeto item en la lista
	 * @param item el objeto a buscar
	 * @return La posicion en la que esta o -1 si no lo encuentra
	 */
	public int search(Object item){
		int pos = -1;
		boolean success = false;
		
		final Iterator<Trie> it = this.childrens.iterator();
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
	/**
	 * Metodo que devuelve True o False para indicar si el objeto es Root
	 * @return True si el objeto es Null, False en caso contrario
	 */
	public boolean isRoot(){
		return (this.root.equals(null));
	}
	/**
	 * Devuelve el objeto Trie en la pos que se indica
	 * @param pos La posicion
	 * @return El objeto
	 */
	public Trie getChild(int pos){
		return ((Trie)this.childrens.get(pos));
	}
	/**
	 * Concatena los valores de la lista en indent
	 * @param indent String al que se iran concatenando los valores de la lista
	 * @return Un String que contendra el string inicial indent + los valores d ela lista
	 */
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
		Iterator<Trie> it = this.childrens.iterator();
		while (it.hasNext()){
			ret += ((Trie)it.next()).toString(indent + "  "); 
		}
		return ret;
	}
	
	/**
	 * Si la lista tiene un solo elemento lo devuelve. Si llega al final, también.
	 * En caso de que haya mas elementos en la lista los va analizando hasta que se den los casos anteriores. 
	 * @param input El array de objetos tipo Char
	 * @param current 
	 * @return El objeto token, 
	 * si el metodo se ha ejecutado bien tendra valor, si no, devolvera null
	 */
	public Token  trackRecursive(char[] input, int current){
		Token ret = null;
		if (this.isLeaf() && this.isTerminal()){
			ret = (Token)this.data;
		}
		else{
			Iterator<Trie> it = this.childrens.iterator();
			while (it.hasNext() && current<input.length){
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
