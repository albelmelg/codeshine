package codeshine.utils;
/**
 * Se establecen los metodos necesarios para poder usar los objetos de tipo Token
 *
 */
public class Token {
	private String value;
	private String replacement;
	private String info;

/**
 * Constructor vacio
 */
	public Token(){
		this.value = "";
		this.replacement = "";
		this.info = "";
	}
/**
 * Constructor de objeto token
 * @param value String
 * @param replacement String
 * @param info String
 */
	public Token(String value, String replacement, String info){
		this.value = value;
		this.replacement = replacement;
		this.info = info;
	}
	
	/**
	 * Construye una cadena de String con los datos del objeto
	 */
	public String toString(){
		return (this.value + "  " + this.replacement + "  " + this.info + "\n");
	}
	/**
	 * Devuelve el valor que hay en Value
	 * @return Un string
	 */
	public String getValue(){
		return this.value;
	}
	/**
	 * Devuelve el valor que hay en Replacement
	 * @return Un string
	 */
	public String getReplacement(){
		return this.replacement;
	}
	/**
	 * Devuelve el valor que hay en info
	 * @return Un string
	 */
	public String getInfo(){
		return this.info;
	}
	/**
	 * Establece un nuevo valor para value
	 * @param value un string del objeto Token
	 */
	public void setValue(String value){
		this.value = value;
	}
	/**
	 * Establece un nuevo valor para info
	 * @param info un string del objeto Token
	 */
	public void setInfo(String info){
		this.info = info;
	}
	/**
	 * Establece un nuevo valor para replacement
	 * @param replacement un string del objeto Token
	 */
	public void setReplacement(String replacement){
		this.replacement = replacement;
	}
	/**
	 * Devuelve True o False si los parametros value de dos objetos token son iguales
	 * @param token el objeto token
	 * @return True si son iguales, False si no los son
	 */
	public boolean equals(Token token){
		return this.value.equals(token.getValue());
	}
}

