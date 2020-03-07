package com.bbsmart.pda.blackberry.bbphoto.ui.customfields;

import java.util.Vector;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;

import com.bbsmart.pda.blackberry.bbphoto.BBSmartPhoto;
import com.bbsmart.pda.blackberry.bbphoto.models.Album;
import com.bbsmart.pda.blackberry.bbphoto.models.AlbumPicture;
import com.bbsmart.pda.blackberry.bbphoto.util.ImageFileUtil;
import com.bbsmart.pda.blackberry.bbphoto.util.Themes;
import com.bbsmart.pda.blackberry.bbphoto.util.UiUtil;

public final class ListView extends ListField implements BBPhotoView, ListFieldCallback {
	private boolean alive = false;

	public static Bitmap background = ImageFileUtil.getThemeImage(UiUtil.getListField());
	private Album album;
	
	public ListView(Album a) {
		super();
		album = a;
		setFont(getFont().derive(Font.BOLD));
		setCallback(this);
	}
	
	public void run() {
		alive = true;
		for(int i = 0; i < album.getSize(); i++) {
			add(album.getPicture(i));
		}
		synchronized(BBSmartPhoto.getEventLock()) {
			setSize(getSize());
		}
		alive = false;
	}
	
	public boolean isAlive() { return alive; }
	
	public boolean isSelecting() { return false; }
	
	public Field getAsField() {
		return this;
	}
	
	public void deletePicture(String path) {
		int index = indexOfList(this, path, 0);
		if(index < 0) { return; }
		delete(index);
		if(getSize() > 0) {
			this.invalidateRange(index, getSize()-1);
		}
	}

	public void moveFocusedPicture(int dx, int dy) {
		// ListView maintains alphabetical ordering so moving is not allowed
	}
	
	public Album getAlbum() {
		return album;
	}
	
	public int getAlbumIndex() {
		return album.getIndexOfPicture(getFocusedPicture());
	}
	
	public String getFocusedPicture() {
		if(getSelectedIndex() < 0) { return null; }
		return (String)get(this, getSelectedIndex());
	}
	
	public Vector getSelectedPictures() {
		Vector v = new Vector();
		v.addElement(getFocusedPicture());
		return v;
	}

	public String getNote() {
		String path = getFocusedPicture();
		if(path == null) {
			return null;
		}
		AlbumPicture p = album.getPicture(path);
		if(p == null) {
			return null;
		}
		return p.getNote();
	}
	
	public String trackImage() {
		return "(" + (getSelectedIndex()+1) + "/" + getSize() + ")";
	}
	
	public void update(String path, String oldPath) {
		int index = indexOfList(this, oldPath, 0);
		replace(path, index);
		this.invalidate(index);
	}
	
	public void updateBorder() {}
	
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
		if(!listElements.isEmpty()) {
			int index = 0;
			while(index < listElements.size()) {
				String text = (String)listElements.elementAt(index);
				if((element.toString()).toLowerCase().compareTo(    
						(text.substring(text.lastIndexOf('/') + 1, text.lastIndexOf('.'))).toLowerCase()) < 0) {
					listElements.insertElementAt(((AlbumPicture)element).getPath(), index);
					return;
				}
				index = index + 1;
			}
		}
		listElements.addElement(((AlbumPicture)element).getPath());
	}
	
	public void replace(Object element, int index) {
		listElements.setElementAt(element, index);
	}
	
	public void delete(int index) {
		listElements.removeElementAt(index);
		setSize(getSize(), index);
	}
	
	public void drawListRow(ListField listField, Graphics graphics, int index, int y, int width) {
		if(index == listField.getSelectedIndex() && isFocus) {
			graphics.setColor(Themes.getCurrentTheme().getTextFocusColor());
			graphics.drawRect(0, y, width, getRowHeight());
		} else {
			graphics.drawBitmap(0, y, background.getWidth(), background.getHeight(), background, 0, 0);
			graphics.setColor(Themes.getCurrentTheme().getTextUnfocusColor());
		}
		String text = (String)listElements.elementAt(index);
		text = text.substring(text.lastIndexOf('/')+1 , text.lastIndexOf('.'));
        graphics.drawText(text, 10, y, 0, width);
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