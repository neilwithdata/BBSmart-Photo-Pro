package com.bbsmart.pda.blackberry.bbphoto.ui.customfields;

import java.util.Vector;

import com.bbsmart.pda.blackberry.bbphoto.models.AlbumList;
import com.bbsmart.pda.blackberry.bbphoto.util.Themes;
import com.bbsmart.pda.blackberry.bbphoto.util.UiUtil;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;

public final class AlbumListField extends ListField implements ListFieldCallback {
	
	public AlbumListField() {
		super();
		setCallback(this);
		
		String[] albums = AlbumList.getInstance().listAlbumsWithDetails();
		for(int i = 0; i < albums.length; i++) {
			add(albums[i]);
		}
		setFont(getFont().derive(Font.BOLD));
		setRowHeight(getFont().getHeight()*2+8);
		setSize(getSize());
	}
	
	public void update() {
		listElements = new Vector();
		String[] albums = AlbumList.getInstance().listAlbumsWithDetails();
		for(int i = 0; i < albums.length; i++) {
			add(albums[i]);
		}
		setSize(getSize());
		this.invalidate();
	}
	
	// ******************* GRAPHICS AND FOCUS METHODS ***************
	private boolean isFocus = false;		// Need to do this explicitly since RIM's is always false
	
	protected void drawFocus(Graphics graphics, boolean on) {
		if(on) {
			this.invalidateRange(getSelectedIndex()-1, getSelectedIndex()+1);
		}
	}
	
	protected void onFocus(int direction) {
		isFocus = true;
	}
	
	protected void onUnfocus() {
		isFocus = false;
	}
	
	// ************************ CALLBACK *****************************
	private Vector listElements = new Vector();
	
	public void add(Object element) {
		listElements.addElement(element);
	}
	
	public void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
		// Border and Fill
		if(index == listField.getSelectedIndex() && isFocus) {
			graphics.setColor(Themes.getCurrentTheme().getTextFocusColor());
			graphics.setGlobalAlpha(100);
			graphics.fillRoundRect(3, y+3, width-6, getRowHeight()-6, 10, 10);
			graphics.setGlobalAlpha(255);
		}
		graphics.setColor(Themes.getCurrentTheme().getTextUnfocusColor());
		graphics.drawRoundRect(1, y+1, width-2, getRowHeight()-2, 10, 10);
		graphics.drawRoundRect(2, y+2, width-4, getRowHeight()-4, 10, 10);
		// Contents
		String text = (String)listElements.elementAt(index);
		String name = text.substring(0, text.lastIndexOf(' '));
		String numPics = text.substring(text.lastIndexOf(' ')+2, text.lastIndexOf(')'));
		boolean priv = text.endsWith("p");

		graphics.drawText(name, 10, y+4, 0, width);
        graphics.drawText("Number of Pictures: " + numPics, 10, y + 4 + graphics.getFont().getHeight(), 0, width);
        if(priv) {
        	graphics.drawBitmap(width-28, y+8, 18, 25, UiUtil.getPrivateIcon(), 0, 0);
        }
	}
	
	public Object get(ListField listField, int index) {
		return listElements.elementAt(index);
	}
	
	public int getPreferredWidth(ListField listField) {
		return Graphics.getScreenWidth();
	}
	
	public int getSize() {
		return listElements.size();
	}
	
	public int indexOfList(ListField listField, String prefix, int start) {
		return listField.indexOfList(prefix,start);
	}
}