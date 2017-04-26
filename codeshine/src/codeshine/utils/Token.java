package codeshine.utils;

public class Token {
	private String value;
	private String replacement;
	private String info;

	
	public Token(){
		this.value = "";
		this.replacement = "";
		this.info = "";
	}
	public Token(String value, String replacement, String info){
		this.value = value;
		this.replacement = replacement;
		this.info = info;
	}
	public String toString(){
		return (this.value + "  " + this.replacement + "  " + this.info + "\n");
	}
	public String getValue(){
		return this.value;
	}
	public String getReplacement(){
		return this.replacement;
	}
	public String getInfo(){
		return this.info;
	}
	public void setValue(String value){
		this.value = value;
	}
	public void setInfo(String info){
		this.info = info;
	}
	public void setReplacement(String replacement){
		this.replacement = replacement;
	}
	public boolean equals(Token token){
		return (this.value.equals(token.getValue()));
	}
}

