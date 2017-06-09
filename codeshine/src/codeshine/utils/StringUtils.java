/*******************************************************************************
 * Copyright notice                                                            *
 *                                                                             *
 * Copyright (c) 2005 Feed'n Read Development Team                             *
 * http://sourceforge.net/fnr                                                  *
 *                                                                             *
 * All rights reserved.                                                        *
 *                                                                             *
 * This program and the accompanying materials are made available under the    *
 * terms of the Common Public License v1.0 which accompanies this distribution,*
 * and is available at                                                         *
 * http://www.eclipse.org/legal/cpl-v10.html                                   *
 *                                                                             *
 * A copy is found in the file cpl-v10.html and important notices to the       *
 * license from the team is found in the textfile LICENSE.txt distributed      *
 * in this package.                                                            *
 *                                                                             *
 * This copyright notice MUST APPEAR in all copies of the file.                *
 *                                                                             *
 * Contributors:                                                               *
 *    Feed'n Read - initial API and implementation                             *
 *                  (smachhau@users.sourceforge.net)                           *
 *******************************************************************************/

package codeshine.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.swt.graphics.Point;


/**
 * Contiene metodos de utilidad relacionados con el uso de Strings
 * @author <a href="mailto:smachhau@users.sourceforge.net">Sebastian Machhausen</a>
 */
public class StringUtils {

	
	/**
	 * 
	 * Lo larga que puede ser una entidad
	 * @see #SHORTEST_ENTITY
	 */
	private final static int LONGEST_ENTITY = 10;

	/**
	 * Lo corta que puede ser una entidad.
	 * @see #LONGEST_ENTITY
	 */
    private final static int SHORTEST_ENTITY = 4; /* &#1; &lt; */

    /**
	 * Asigna el nombre de la entidad a su correspondiente caracter unicode. 
	 */
	private final static Map entity_mapping;
	
	/**
	 *Patron de expresion regular que se usa para coincidir con cualquier etiqueta HTML
	 * @see #stripHTML(String)
	 * @see #convertHTML(String)
	 */
	private final static String HTML_PATTERN = "(<[^>]+>)";
    
    /** Constante para Strings vacios */
    public final static String EMPTY_STRING = "";
    
    /** Contante para las lineas separadoras */
    public final static String LINE_SEPARATOR = 
        System.getProperty("line.separator");
    
    /** Delimitador por defecto para char */
    public final static char DEFAULT_DELIMITER = ',';
    
	
	/**
	 * El bloque de inicilizacion estatica crea la tabla de asignación de entidades internas
	 * 
	 */
	static {
		String[] entityKeys = { "quot"
		/* 34 */, "amp"
		/* 38 */, "lt"
		/* 60 */, "gt"
		/* 62 */, "nbsp"
		/* 160 */, "iexcl"
		/* 161 */, "cent"
		/* 162 */, "pound"
		/* 163 */, "curren"
		/* 164 */, "yen"
		/* 165 */, "brvbar"
		/* 166 */, "sect"
		/* 167 */, "uml"
		/* 168 */, "copy"
		/* 169 */, "ordf"
		/* 170 */, "laquo"
		/* 171 */, "not"
		/* 172 */, "shy"
		/* 173 */, "reg"
		/* 174 */, "macr"
		/* 175 */, "deg"
		/* 176 */, "plusmn"
		/* 177 */, "sup2"
		/* 178 */, "sup3"
		/* 179 */, "acute"
		/* 180 */, "micro"
		/* 181 */, "para"
		/* 182 */, "middot"
		/* 183 */, "cedil"
		/* 184 */, "sup1"
		/* 185 */, "ordm"
		/* 186 */, "raquo"
		/* 187 */, "frac14"
		/* 188 */, "frac12"
		/* 189 */, "frac34"
		/* 190 */, "iquest"
		/* 191 */, "Agrave"
		/* 192 */, "Aacute"
		/* 193 */, "Acirc"
		/* 194 */, "Atilde"
		/* 195 */, "Auml"
		/* 196 */, "Aring"
		/* 197 */, "AElig"
		/* 198 */, "Ccedil"
		/* 199 */, "Egrave"
		/* 200 */, "Eacute"
		/* 201 */, "Ecirc"
		/* 202 */, "Euml"
		/* 203 */, "Igrave"
		/* 204 */, "Iacute"
		/* 205 */, "Icirc"
		/* 206 */, "Iuml"
		/* 207 */, "ETH"
		/* 208 */, "Ntilde"
		/* 209 */, "Ograve"
		/* 210 */, "Oacute"
		/* 211 */, "Ocirc"
		/* 212 */, "Otilde"
		/* 213 */, "Ouml"
		/* 214 */, "times"
		/* 215 */, "Oslash"
		/* 216 */, "Ugrave"
		/* 217 */, "Uacute"
		/* 218 */, "Ucirc"
		/* 219 */, "Uuml"
		/* 220 */, "Yacute"
		/* 221 */, "THORN"
		/* 222 */, "szlig"
		/* 223 */, "agrave"
		/* 224 */, "aacute"
		/* 225 */, "acirc"
		/* 226 */, "atilde"
		/* 227 */, "auml"
		/* 228 */, "aring"
		/* 229 */, "aelig"
		/* 230 */, "ccedil"
		/* 231 */, "egrave"
		/* 232 */, "eacute"
		/* 233 */, "ecirc"
		/* 234 */, "euml"
		/* 235 */, "igrave"
		/* 236 */, "iacute"
		/* 237 */, "icirc"
		/* 238 */, "iuml"
		/* 239 */, "eth"
		/* 240 */, "ntilde"
		/* 241 */, "ograve"
		/* 242 */, "oacute"
		/* 243 */, "ocirc"
		/* 244 */, "otilde"
		/* 245 */, "ouml"
		/* 246 */, "divide"
		/* 247 */, "oslash"
		/* 248 */, "ugrave"
		/* 249 */, "uacute"
		/* 250 */, "ucirc"
		/* 251 */, "uuml"
		/* 252 */, "yacute"
		/* 253 */, "thorn"
		/* 254 */, "yuml"
		/* 255 */, "OElig"
		/* 338 */, "oelig"
		/* 339 */, "Scaron"
		/* 352 */, "scaron"
		/* 353 */, "Yuml"
		/* 376 */, "fnof"
		/* 402 */, "circ"
		/* 710 */, "tilde"
		/* 732 */, "Alpha"
		/* 913 */, "Beta"
		/* 914 */, "Gamma"
		/* 915 */, "Delta"
		/* 916 */, "Epsilon"
		/* 917 */, "Zeta"
		/* 918 */, "Eta"
		/* 919 */, "Theta"
		/* 920 */, "Iota"
		/* 921 */, "Kappa"
		/* 922 */, "Lambda"
		/* 923 */, "Mu"
		/* 924 */, "Nu"
		/* 925 */, "Xi"
		/* 926 */, "Omicron"
		/* 927 */, "Pi"
		/* 928 */, "Rho"
		/* 929 */, "Sigma"
		/* 931 */, "Tau"
		/* 932 */, "Upsilon"
		/* 933 */, "Phi"
		/* 934 */, "Chi"
		/* 935 */, "Psi"
		/* 936 */, "Omega"
		/* 937 */, "alpha"
		/* 945 */, "beta"
		/* 946 */, "gamma"
		/* 947 */, "delta"
		/* 948 */, "epsilon"
		/* 949 */, "zeta"
		/* 950 */, "eta"
		/* 951 */, "theta"
		/* 952 */, "iota"
		/* 953 */, "kappa"
		/* 954 */, "lambda"
		/* 955 */, "mu"
		/* 956 */, "nu"
		/* 957 */, "xi"
		/* 958 */, "omicron"
		/* 959 */, "pi"
		/* 960 */, "rho"
		/* 961 */, "sigmaf"
		/* 962 */, "sigma"
		/* 963 */, "tau"
		/* 964 */, "upsilon"
		/* 965 */, "phi"
		/* 966 */, "chi"
		/* 967 */, "psi"
		/* 968 */, "omega"
		/* 969 */, "thetasym"
		/* 977 */, "upsih"
		/* 978 */, "piv"
		/* 982 */, "ensp"
		/* 8194 */, "emsp"
		/* 8195 */, "thinsp"
		/* 8201 */, "zwnj"
		/* 8204 */, "zwj"
		/* 8205 */, "lrm"
		/* 8206 */, "rlm"
		/* 8207 */, "ndash"
		/* 8211 */, "mdash"
		/* 8212 */, "lsquo"
		/* 8216 */, "rsquo"
		/* 8217 */, "sbquo"
		/* 8218 */, "ldquo"
		/* 8220 */, "rdquo"
		/* 8221 */, "bdquo"
		/* 8222 */, "dagger"
		/* 8224 */, "Dagger"
		/* 8225 */, "bull"
		/* 8226 */, "hellip"
		/* 8230 */, "permil"
		/* 8240 */, "prime"
		/* 8242 */, "Prime"
		/* 8243 */, "lsaquo"
		/* 8249 */, "rsaquo"
		/* 8250 */, "oline"
		/* 8254 */, "frasl"
		/* 8260 */, "euro"
		/* 8364 */, "image"
		/* 8465 */, "weierp"
		/* 8472 */, "real"
		/* 8476 */, "trade"
		/* 8482 */, "alefsym"
		/* 8501 */, "larr"
		/* 8592 */, "uarr"
		/* 8593 */, "rarr"
		/* 8594 */, "darr"
		/* 8595 */, "harr"
		/* 8596 */, "crarr"
		/* 8629 */, "lArr"
		/* 8656 */, "uArr"
		/* 8657 */, "rArr"
		/* 8658 */, "dArr"
		/* 8659 */, "hArr"
		/* 8660 */, "forall"
		/* 8704 */, "part"
		/* 8706 */, "exist"
		/* 8707 */, "empty"
		/* 8709 */, "nabla"
		/* 8711 */, "isin"
		/* 8712 */, "notin"
		/* 8713 */, "ni"
		/* 8715 */, "prod"
		/* 8719 */, "sum"
		/* 8721 */, "minus"
		/* 8722 */, "lowast"
		/* 8727 */, "radic"
		/* 8730 */, "prop"
		/* 8733 */, "infin"
		/* 8734 */, "ang"
		/* 8736 */, "and"
		/* 8743 */, "or"
		/* 8744 */, "cap"
		/* 8745 */, "cup"
		/* 8746 */, "int"
		/* 8747 */, "there4"
		/* 8756 */, "sim"
		/* 8764 */, "cong"
		/* 8773 */, "asymp"
		/* 8776 */, "ne"
		/* 8800 */, "equiv"
		/* 8801 */, "le"
		/* 8804 */, "ge"
		/* 8805 */, "sub"
		/* 8834 */, "sup"
		/* 8835 */, "nsub"
		/* 8836 */, "sube"
		/* 8838 */, "supe"
		/* 8839 */, "oplus"
		/* 8853 */, "otimes"
		/* 8855 */, "perp"
		/* 8869 */, "sdot"
		/* 8901 */, "lceil"
		/* 8968 */, "rceil"
		/* 8969 */, "lfloor"
		/* 8970 */, "rfloor"
		/* 8971 */, "lang"
		/* 9001 */, "rang"
		/* 9002 */, "loz"
		/* 9674 */, "spades"
		/* 9824 */, "clubs"
		/* 9827 */, "hearts"
		/* 9829 */, "diams"
		/* 9830 */, };
				
		char[] entityValues = { 34
		/* &quot; */, 38
		/* &amp; */, 60
		/* &lt; */, 62
		/* &gt; */, 160
		/* &nbsp; */, 161
		/* &iexcl; */, 162
		/* &cent; */, 163
		/* &pound; */, 164
		/* &curren; */, 165
		/* &yen; */, 166
		/* &brvbar; */, 167
		/* &sect; */, 168
		/* &uml; */, 169
		/* &copy; */, 170
		/* &ordf; */, 171
		/* &laquo; */, 172
		/* &not; */, 173
		/* &shy; */, 174
		/* &reg; */, 175
		/* &macr; */, 176
		/* &deg; */, 177
		/* &plusmn; */, 178
		/* &sup2; */, 179
		/* &sup3; */, 180
		/* &acute; */, 181
		/* &micro; */, 182
		/* &para; */, 183
		/* &middot; */, 184
		/* &cedil; */, 185
		/* &sup1; */, 186
		/* &ordm; */, 187
		/* &raquo; */, 188
		/* &frac14; */, 189
		/* &frac12; */, 190
		/* &frac34; */, 191
		/* &iquest; */, 192
		/* &Agrave; */, 193
		/* &Aacute; */, 194
		/* &Acirc; */, 195
		/* &Atilde; */, 196
		/* &Auml; */, 197
		/* &Aring; */, 198
		/* &AElig; */, 199
		/* &Ccedil; */, 200
		/* &Egrave; */, 201
		/* &Eacute; */, 202
		/* &Ecirc; */, 203
		/* &Euml; */, 204
		/* &Igrave; */, 205
		/* &Iacute; */, 206
		/* &Icirc; */, 207
		/* &Iuml; */, 208
		/* &ETH; */, 209
		/* &Ntilde; */, 210
		/* &Ograve; */, 211
		/* &Oacute; */, 212
		/* &Ocirc; */, 213
		/* &Otilde; */, 214
		/* &Ouml; */, 215
		/* &times; */, 216
		/* &Oslash; */, 217
		/* &Ugrave; */, 218
		/* &Uacute; */, 219
		/* &Ucirc; */, 220
		/* &Uuml; */, 221
		/* &Yacute; */, 222
		/* &THORN; */, 223
		/* &szlig; */, 224
		/* &agrave; */, 225
		/* &aacute; */, 226
		/* &acirc; */, 227
		/* &atilde; */, 228
		/* &auml; */, 229
		/* &aring; */, 230
		/* &aelig; */, 231
		/* &ccedil; */, 232
		/* &egrave; */, 233
		/* &eacute; */, 234
		/* &ecirc; */, 235
		/* &euml; */, 236
		/* &igrave; */, 237
		/* &iacute; */, 238
		/* &icirc; */, 239
		/* &iuml; */, 240
		/* &eth; */, 241
		/* &ntilde; */, 242
		/* &ograve; */, 243
		/* &oacute; */, 244
		/* &ocirc; */, 245
		/* &otilde; */, 246
		/* &ouml; */, 247
		/* &divide; */, 248
		/* &oslash; */, 249
		/* &ugrave; */, 250
		/* &uacute; */, 251
		/* &ucirc; */, 252
		/* &uuml; */, 253
		/* &yacute; */, 254
		/* &thorn; */, 255
		/* &yuml; */, 338
		/* &OElig; */, 339
		/* &oelig; */, 352
		/* &Scaron; */, 353
		/* &scaron; */, 376
		/* &Yuml; */, 402
		/* &fnof; */, 710
		/* &circ; */, 732
		/* &tilde; */, 913
		/* &Alpha; */, 914
		/* &Beta; */, 915
		/* &Gamma; */, 916
		/* &Delta; */, 917
		/* &Epsilon; */, 918
		/* &Zeta; */, 919
		/* &Eta; */, 920
		/* &Theta; */, 921
		/* &Iota; */, 922
		/* &Kappa; */, 923
		/* &Lambda; */, 924
		/* &Mu; */, 925
		/* &Nu; */, 926
		/* &Xi; */, 927
		/* &Omicron; */, 928
		/* &Pi; */, 929
		/* &Rho; */, 931
		/* &Sigma; */, 932
		/* &Tau; */, 933
		/* &Upsilon; */, 934
		/* &Phi; */, 935
		/* &Chi; */, 936
		/* &Psi; */, 937
		/* &Omega; */, 945
		/* &alpha; */, 946
		/* &beta; */, 947
		/* &gamma; */, 948
		/* &delta; */, 949
		/* &epsilon; */, 950
		/* &zeta; */, 951
		/* &eta; */, 952
		/* &theta; */, 953
		/* &iota; */, 954
		/* &kappa; */, 955
		/* &lambda; */, 956
		/* &mu; */, 957
		/* &nu; */, 958
		/* &xi; */, 959
		/* &omicron; */, 960
		/* &pi; */, 961
		/* &rho; */, 962
		/* &sigmaf; */, 963
		/* &sigma; */, 964
		/* &tau; */, 965
		/* &upsilon; */, 966
		/* &phi; */, 967
		/* &chi; */, 968
		/* &psi; */, 969
		/* &omega; */, 977
		/* &thetasym; */, 978
		/* &upsih; */, 982
		/* &piv; */, 8194
		/* &ensp; */, 8195
		/* &emsp; */, 8201
		/* &thinsp; */, 8204
		/* &zwnj; */, 8205
		/* &zwj; */, 8206
		/* &lrm; */, 8207
		/* &rlm; */, 8211
		/* &ndash; */, 8212
		/* &mdash; */, 8216
		/* &lsquo; */, 8217
		/* &rsquo; */, 8218
		/* &sbquo; */, 8220
		/* &ldquo; */, 8221
		/* &rdquo; */, 8222
		/* &bdquo; */, 8224
		/* &dagger; */, 8225
		/* &Dagger; */, 8226
		/* &bull; */, 8230
		/* &hellip; */, 8240
		/* &permil; */, 8242
		/* &prime; */, 8243
		/* &Prime; */, 8249
		/* &lsaquo; */, 8250
		/* &rsaquo; */, 8254
		/* &oline; */, 8260
		/* &frasl; */, 8364
		/* &euro; */, 8465
		/* &image; */, 8472
		/* &weierp; */, 8476
		/* &real; */, 8482
		/* &trade; */, 8501
		/* &alefsym; */, 8592
		/* &larr; */, 8593
		/* &uarr; */, 8594
		/* &rarr; */, 8595
		/* &darr; */, 8596
		/* &harr; */, 8629
		/* &crarr; */, 8656
		/* &lArr; */, 8657
		/* &uArr; */, 8658
		/* &rArr; */, 8659
		/* &dArr; */, 8660
		/* &hArr; */, 8704
		/* &forall; */, 8706
		/* &part; */, 8707
		/* &exist; */, 8709
		/* &empty; */, 8711
		/* &nabla; */, 8712
		/* &isin; */, 8713
		/* &notin; */, 8715
		/* &ni; */, 8719
		/* &prod; */, 8721
		/* &sum; */, 8722
		/* &minus; */, 8727
		/* &lowast; */, 8730
		/* &radic; */, 8733
		/* &prop; */, 8734
		/* &infin; */, 8736
		/* &ang; */, 8743
		/* &and; */, 8744
		/* &or; */, 8745
		/* &cap; */, 8746
		/* &cup; */, 8747
		/* &int; */, 8756
		/* &there4; */, 8764
		/* &sim; */, 8773
		/* &cong; */, 8776
		/* &asymp; */, 8800
		/* &ne; */, 8801
		/* &equiv; */, 8804
		/* &le; */, 8805
		/* &ge; */, 8834
		/* &sub; */, 8835
		/* &sup; */, 8836
		/* &nsub; */, 8838
		/* &sube; */, 8839
		/* &supe; */, 8853
		/* &oplus; */, 8855
		/* &otimes; */, 8869
		/* &perp; */, 8901
		/* &sdot; */, 8968
		/* &lceil; */, 8969
		/* &rceil; */, 8970
		/* &lfloor; */, 8971
		/* &rfloor; */, 9001
		/* &lang; */, 9002
		/* &rang; */, 9674
		/* &loz; */, 9824
		/* &spades; */, 9827
		/* &clubs; */, 9829
		/* &hearts; */, 9830
		/* &diams; */, };
				
		entity_mapping = new HashMap(511);
		for (int i = 0; i < entityKeys.length; i++) {
			entity_mapping.put(entityKeys[i], new Character(entityValues[i]));
		}
	} // end static initialization block

	
    /**
	 * Convierte el string que recibe a un char especifico
	 * 
	 * @param entity. La entidad String a convertir. Trabaja mas deprisa si
	 * es en minuscula.
	 * 
	 * @return El caracter equivalete o 0 si no se conoce a entidad.
	 */
    public static char entityToChar(String entity) {
		Character code = (Character) entity_mapping.get(entity);
		if (code != null) {
			return code.charValue();
		}		
		code = (Character) entity_mapping.get(entity.toLowerCase());
		if (code != null) {
			return code.charValue();
		}		
		/* Check at least having &#1; */
		if (entity.length() < 2) {
			return 0;
		}
		
		try {
			switch (entity.charAt(0)) {
			case 'x':
			case 'X':
				/* Handle entities denoted in hexadecimal */
				if (entity.charAt(1) != '#') {
					return 0;
				}
				/* Ensure at least having &x#1; */
				if (entity.length() < 3) {
					return 0;
				}
				return (char) Integer.parseInt(entity.substring(2), 16);
			case '#':
				/* Handle decimal entities */
				return ((char) Integer.parseInt(entity.substring(1)));
			default:
				return 0;
			}
		} catch (NumberFormatException e) {
			return 0;
		}
	} // end method entityToChar(String)

    
    /**
	 * Convierte HTML a entidades de conversion de texto como
	 * &quot; back to " y
	 * &lt; back to &lt; El texto ordinario no se cambia.
	 * 
	 * 
	 * @param text Es el String que representa el texto que va a ser procesado. No puede ser null
	 * 
	 * @return El texto convertido; HTML 4.0. Si se le pasa un texto null, devuelve null.
	 */
    public static String convertEntities(String text) {
		if (text == null) {
			return null;
		}
		if (text.indexOf('&') < 0) {
			/* There are no entities, nothing to do */
			return text;
		}
		int originalTextLength = text.length();
		StringBuffer stringbuffer = new StringBuffer(originalTextLength);
		for (int i = 0; i < originalTextLength; i++) {
			int whereAmp = text.indexOf('&', i);
			if (whereAmp < 0) {
				/* no more &s, we are done: Append all remaining text */
				stringbuffer.append(text.substring(i));
				break;
			} else {
				/* Append all text to left of next & */
				stringbuffer.append(text.substring(i, whereAmp));
				/* Avoid reprocessing those chars */
				i = whereAmp;
				/* text.charAt(i) is an & possEntity has lead & stripped. */
				String possEntity = text.substring(i + 1, Math.min(i
						+ LONGEST_ENTITY, text.length()));
				char auxiliar = potentialEntityToChar(possEntity);
				if (auxiliar != 0) {
					/* It was a good entity, keep its equivalent char. */
					stringbuffer.append(auxiliar);
					/* Avoid reprocessing chars forming the entity */
					int whereSemi = possEntity
							.indexOf(";", SHORTEST_ENTITY - 2);
					i += whereSemi + 1;
				} else {
					/* Treat & just as ordinary character */
					stringbuffer.append('&');
				}
			} 
		}
		/* If result is not shorter, we did not do anything. Saves RAM. */
		return (stringbuffer.length() == originalTextLength) ? text : stringbuffer.toString();
	} // end method convertEntities(String)
    
    
    /**
	 * Quita todas las etiquetas HTML del texto
	 * @param text El String al que hay que quitarle las etiquetas HTML
	 * 
	 * @return el texto con las etiquetas quitadas o null 
	 * si el texto que se le ha pasado es null 
	 */
    public static String stripHTML(String text) {
    	if (text != null) {
    		return text.replaceAll(HTML_PATTERN, EMPTY_STRING);
    	} else {
    		return null;
    	}
    } // end method stripHTML(String)
    
    
    /**
	 * Convierte el HTML que se le pasa a a texto puro.
	 * Todas las entidades son convertidas y las etiquetas quitadas.
	 * 
	 * @param text  El string a convertir. Si es null se convertira a un String vacio.
	 * @return el texto convertido
	 * 
	 * @see #convertEntities(String)
	 * @see #stripHTML(String)
	 */
    public static String convertHTML(String text) {
    	text = nullValueToEmptyString(text);
    	text = convertEntities(text);
    	text = stripHTML(text);
    	
    	return text;
    } // end method convertHTML(String)
    
    
    /**
	 * Convierte string que es null a un objeto String vacio.
	 *  Si el valor no es null
	 * solo se recorta usando {@link java.lang.String#trim}
	 * 
	 * @param str EL String a convertir
	 * 
	 * @return Un String vacio si el valor que se le ha pasado es null.
	 * De lo contrario se recortara usando {@link java.lang.String#trim}
	 */
    public static String nullValueToEmptyString(String str) {
       return (str == null ? EMPTY_STRING : str.trim());
   } // end method nullValueToEmptyString(String)

    
   /**
	 * Comprueba si el String que se le pasa esta vacio.
	 * 
	 * @param str El string que comprobar
	 * @return True si esta vacio y False en el caso contrario
	 */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    } // end method isEmpty(String)
    
    
    /**
     * Se recorta el String que se le pasa si sobrepasa el valor de maximumChars.
     * Si la longitud del String es mayor a ese valor se recorta. El resultado
     * del String sera un substring de str que empezará en el indice 0
     * y acabará en el indice maximunChars concatenado con "..." para mostrar que la
     * abrebiación se ha hecho. Si la longitud es menor a la que se le pasa
     * la cadena se mantiene intacta.
     * 
     * @param str El String a recortar.. 
     * @param maximumChars El maximo de caracteres.
     * @return El String recortado.
     */
    public static String abbreviate(final String str, int maximumChars) {
        if (!isEmpty(str)) {
            if (str.length() > maximumChars) {
                return (str.substring(0, maximumChars) + "...");
            } 
        }
        return str;
    } // end method abbreviate(String, int)
    
    
    /**
     * Convierte una cadema de string en un solo String en el cual los elementos
     * estan seprados por un delimitador
     * 
     * @param input El array de Strings
     * @param delimiter Es el delimitador
     * 
     * @return un solo String cuyos elementos estan separados por el delimitador
     * 
     * @see #arrayToCSV(int[], char)
     * @see #arrayToCSV(Point[], char)
     */
    public static String arrayToCSV(String[] input, char delimiter) {
        if (input != null) {
            StringBuffer csvList = new StringBuffer();
            for (int i = 0; i < input.length; i++) {
                csvList.append(input[i]);
                if (i != input.length - 1) {
                    csvList.append(delimiter);
                }
            }
            return csvList.toString();
        } else {
            return EMPTY_STRING;
        }
    } // end method arrayToCSV(String[], char)

    
    /**
     * Convierte una cadema de int en un solo String en el cual los elementos
     * estan seprados por un delimitador
     * 
     * @param input El array de int
     * @param delimiter  El delimitador
     * 
     * @return un solo String cuyos elementos estan separados por el delimitador
     * 
     * @see #arrayToCSV(String[], char)
     * @see #arrayToCSV(Point[], char)
     */
    public static String arrayToCSV(int[] input, char delimiter) {
    	if (input != null) {
    		StringBuffer csvList = new StringBuffer();
    		for (int i = 0; i < input.length; i++) {
    			csvList.append(input[i]);
    			if (i != input.length - 1) {
    				csvList.append(delimiter);
    			}
    		}
    		return csvList.toString();
    	} else {
    		return EMPTY_STRING;
    	}
    } // end method arrayToCSV(int[], char)
    
    
    /**
     * Convierte el input en un solo String en el cual los elementos
     * estan separados por un delimitador
     * 
     * @param input Un array de POINT con los elementos
     * @param delimiter  El delimitador
     * 
     * @return un solo String cuyos elementos están separados por el delimitador
     * 
     * @see #arrayToCSV(String[], char)
     * @see #arrayToCSV(int[], char)
     */
    public static String arrayToCSV(Point[] input, char delimiter) {
        if (input != null) {
            StringBuffer csvList = new StringBuffer();
            for (int i = 0; i < input.length; i++) {
                csvList.append(input[i].x);
                csvList.append('.');
                csvList.append(input[i].y);
                if (i != input.length - 1) {
                    csvList.append(delimiter);
                }
            }
            return csvList.toString();
        } else {
            return EMPTY_STRING;
        }
    } // end method arrayToCSV(Point[], char)
    
    
    /**
     * Usa el delimitador para convertir el input en un array
     * de un solo string
     * 
     * @param input  El string cuyos elementos estaran separados
     * por el delimitador 
     * @param delimiter  El delimitador
     * 
     * @return un array de string con el input separador por el delimitador.
     * 
     * @see #csvToIntArray(String, char)
     * @see #csvToPointArray(String, char)
     */
    public static String[] csvToArray(final String input, final char delimiter) {
    	StringTokenizer tokenizer = 
    		new StringTokenizer(input, String.valueOf(delimiter));
    	ArrayList list = new ArrayList();

    	while (tokenizer.hasMoreTokens()) {
    		list.add(tokenizer.nextToken());
    	}

    	String[] array = new String[list.size()];
    	return ((String[]) list.toArray(array));
    } // end method csvToArrray(String, char)
    
    
    /**
	 * Usa el delimitador para convertir el input en un array de int.
	 * 
	 * @param input El string cuyos elementos estarán separados por el delimitador
	 * @param delimiter El delimitador
	 * 
	 * @return UN array de int con los valores del input
	 * 
	 * @see #csvToArray(String, char)
	 * @see #csvToPointArray(String, char)
	 */
    public static int[] csvToIntArray(String input, char delimiter) {
    	StringTokenizer tokenizer = 
    		new StringTokenizer(input, String.valueOf(delimiter));
    	int[] list = new int[tokenizer.countTokens()];
    	int valori = 0;

    	while (tokenizer.hasMoreTokens()) {
    		try {
    			list[valori++] = Integer.parseInt(tokenizer.nextToken());
    		} catch (NumberFormatException nfe) {}
    	}

    	return list;
    } // end method csvToIntArrray(String, char)
    
    
    /**
	 * Usa el delimitador para generar un array de Point con los elementos del input
	 * separados por el delimitador 
	 * @param input El string a convertir
	 * @param delimiter El delimitador
	 * 
	 * @return Un array de 'Point' con los elementos del input
	 * 
	 * @see #csvToArray(String, char)
	 * @see #csvToIntArray(String, char)
	 */
    public static Point[] csvToPointArray(String input, final char delimiter) {
    	StringTokenizer tokenizer = 
    		new StringTokenizer(input, String.valueOf(delimiter));
    	Point[] list = new Point[tokenizer.countTokens()];
    	int indice = 0;
    	String item;
    	int index = -1;
    	int valorx = 0;
    	int valory = 0;
    	
    	while (tokenizer.hasMoreTokens()) {
    		item = tokenizer.nextToken();
    		index = item.indexOf('.');
    		if (index != -1) {
	    		try {
	    			valorx = Integer.parseInt(item.substring(0, index));
	    			valory = Integer.parseInt(item.substring(index + 1));
	    		} catch (NumberFormatException nfe) {}
    		}
    		list[indice++] = new Point(valorx, valory);
    	}

    	return list;
    } // end method csvToPointArrray(String, char)
    
    
    /**
	 * Comprueba un numero de condiciones para asegurarse que hay una entidad valida.
	 * Convierte el entity que recibe a char.
	 * 
	 * @param entity - El String
	 * 
	 * @return el correspondiente unicode, o 0 si la entidad es invalida.
	 */
    private static char potentialEntityToChar(String entity) {
		if (entity.length() < SHORTEST_ENTITY - 1) {
			return 0;
		}

		/* Find the trailing ; */
		int whereSemi = entity.indexOf(';', SHORTEST_ENTITY - 2); 			
		if (whereSemi < SHORTEST_ENTITY - 2) {
			return 0;
		}

		/*
		 * we found a potential entity, at least it has &xxxx; lead & already
		 * stripped, now strip trailing ; and look it up in a table. Will return
		 * 0 for an invalid entity.
		 */
		return entityToChar(entity.substring(0, whereSemi));
	} // end method potentialEntityToChar(String)
 
    
} // end class StringUtils