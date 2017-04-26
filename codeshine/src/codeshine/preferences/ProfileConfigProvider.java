package codeshine.preferences;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.Viewer;

import codeshine.utils.ITableContentProvider;
import codeshine.utils.TokenList;
import codeshine.utils.Token;

public class ProfileConfigProvider extends ArrayContentProvider implements
		ITableContentProvider {
	
	public ProfileConfigProvider() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Object getColumnValue(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		Token t = null;
		String retValue;
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
		default:{
			retValue = t.toString();
			break;
		}
	}
	return retValue;
	}
//	TODO:Revisar esto
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput){
		System.out.println("inputChanged");
		try{
			if (oldInput instanceof TokenList)
				System.out.println("#TokenList");
			else if (oldInput instanceof Token)
				System.out.println("#Token");
		}catch (NullPointerException e){e.printStackTrace();}
	}
	public Object[] getElements(Object collection){
		TokenList tlist = null;
		try{
			tlist = (TokenList)collection; 
		}catch (ClassCastException castException){castException.printStackTrace();}
	    
		return (tlist.toArray());
		
	}
 
}
