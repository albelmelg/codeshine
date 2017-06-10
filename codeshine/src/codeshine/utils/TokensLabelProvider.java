package codeshine.utils;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class TokensLabelProvider extends LabelProvider implements
		ITableLabelProvider {
	/**
	 * Devuelve el label de la imagen del elemento en la columnIndex
	 * @param element El objeto que representa la fila entera 
	 * o null que indica que no hay ningun objeto
	 * @param columnIndex El indice de la columna
	 * @return el objeto Image o null si no hay nada
	 */
	public Image getColumnImage(Object element, int columnIndex) {

		return null;

	}
	/**
	 * Devuelve el texto del label para la columna dado el elemento que se le pasa
	 * @param element El objeto que representa la fila entera 
	 * o null que indica que no hay ningun objeto
	 * @param columnIndex el indice de la columna
	 * @return String o null Si no hay texto en la columnIndex
	 */
	public String getColumnText(Object element, int columnIndex) {
		Token t = null;
		String retValue = "";
		try{
			t = (Token)element;
		}catch (ClassCastException e){e.printStackTrace();}
		switch (columnIndex) {
			case 0: {
				retValue = t.getValue();
				break;
			}
			case 1: {
				retValue = t.getReplacement();
				break;
			}
			case 2: {
				retValue = t.getInfo();
				break;
			}
		}
		return retValue;
	}

}
