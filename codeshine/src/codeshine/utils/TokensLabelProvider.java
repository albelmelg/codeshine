package codeshine.utils;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class TokensLabelProvider extends LabelProvider implements
		ITableLabelProvider {

	public TokensLabelProvider() {
		// TODO Auto-generated constructor stub
	}

	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;

	
	}

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
