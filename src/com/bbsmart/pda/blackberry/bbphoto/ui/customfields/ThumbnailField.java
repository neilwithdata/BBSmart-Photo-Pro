package com.bbsmart.pda.blackberry.bbphoto.ui.customfields;

import com.bbsmart.pda.blackberry.bbphoto.models.AlbumPicture;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.BitmapField;

public final class ThumbnailField extends BitmapField {
	private AlbumPicture picture;
	private boolean selected = false;
	
	public ThumbnailField(AlbumPicture p) {
		super(p.getThumbnail(), BitmapField.FOCUSABLE);
		picture = p;
	}
	
	public ThumbnailField(AlbumPicture p, long style) {
		super(p.getThumbnail(), style);
		picture = p;
	}
	
	public void updateField(AlbumPicture p) {
		picture = p;
		setBitmap(p.getThumbnail());
	}
	
	public String getAlbumPicture() {
		return picture.getPath();
	}
	
	public void setSelected(boolean status) {
		selected = status;
	}
	
	public boolean isSelecting() {
		return selected;
	}
	
	// ******************** SIZE AND LAYOUT *******************
	public int getPreferredHeight() {
		return AlbumPicture.THUMB_HEIGHT;
	}
	
	public int getPreferredWidth() {
		return AlbumPicture.THUMB_WIDTH;
	}
	
	// ******************** FOCUS AND PAINTING *******************
	protected void drawFocus(Graphics g, boolean on) {}
	
	protected void paint(Graphics g) {
		if(isVisible()) {
			if(selected) { g.setGlobalAlpha(100); }
			super.paint(g);
		}
	}
}