package codeshine.preferences;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.Viewer;

import codeshine.utils.ITableContentProvider;
import codeshine.utils.TokenList;
import codeshine.utils.Token;

/**
 *Configura el perfil
 */

public class ProfileConfigProvider extends ArrayContentProvider implements
		ITableContentProvider {
	
	static Logger logger = Logger.getLogger(ProfileConfigProvider.class);
	/**
	 *Constructor
	 */
	public ProfileConfigProvider() {
		super();
	}

	/**
     * 
     * Devuelve el valor de la columna para 
     * el indice especificado 
     * 
     * @param element el objeto elemento para el cual consulta el valor de la columna
     * @param columnIndex el índice del valor de la columna que queremos consultar.
     * 
     * @return el valor del elemento
     * columnIndex
     */
	public Object getColumnValue(Object element, int columnIndex) {

		Token token = null;
		String retValue;
		try{
			token = (Token)element;
		}catch (ClassCastException e){e.printStackTrace();}
		switch (columnIndex) {
		case 0: {
			retValue = token.getValue();
			break;
		}
		case 1: {
			retValue = token.getReplacement();
			break;
		}
		case 2: {
			retValue = token.getInfo();
			break;
		}
		default:{
			retValue = token.toString();
			break;
		}
	}
	return retValue;
	}
	/**
	 * Notifica que el contenido que proviene del
	 * viewer ha cambiado a un elemento diferente
	 *@param viewer  El visor
	 *@param oldInput  Elemento antiguo
	 *@param newInput  Elemento nuevo
	 *
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput){
		logger.info("inputChanged");
		try{
			if (oldInput instanceof TokenList)
				logger.info("#TokenList");
			else if (oldInput instanceof Token)
				logger.info("#Token");
		}catch (NullPointerException exception){exception.printStackTrace();}
	}
	/**
	 * Devuelve en formato TokenList los objetos que se le pasan.
	 *@param collection  Colección de objectos
	 *@return una Token List con los ebjetos que se reciben
	 */
	public Object[] getElements(Object collection){
		TokenList tlist = null;
		try{
			tlist = (TokenList)collection; 
		}catch (ClassCastException castException){castException.printStackTrace();}
	    
		return tlist.toArray();
		
	}
 
}
