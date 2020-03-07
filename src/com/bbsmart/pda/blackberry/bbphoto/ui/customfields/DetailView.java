package com.bbsmart.pda.blackberry.bbphoto.ui.customfields;

import java.util.Vector;

import net.rim.device.api.system.KeyListener;
import net.rim.device.api.system.KeypadListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;

import com.bbsmart.pda.blackberry.bbphoto.BBSmartPhoto;
import com.bbsmart.pda.blackberry.bbphoto.models.Album;
import com.bbsmart.pda.blackberry.bbphoto.models.AlbumPicture;
import com.bbsmart.pda.blackberry.bbphoto.ui.screens.AlbumViewScreen;
import com.bbsmart.pda.blackberry.bbphoto.util.Themes;

public final class DetailView implements BBPhotoView {
	private DetailManager view;
	private Album album;
	private boolean alive = false;
	private boolean isSelecting = false;
	
	public DetailView(Album a) {
		album = a;
		view = new DetailManager(DetailManager.VERTICAL_SCROLL);
	}
	
	private class DetailManager extends VerticalFieldManager {
    	public DetailManager(long style) {
    		super(style);
    	}
    	
    	private void clearSelected() {
    		if(isSelecting) {
				for(int i = 0; i < this.getFieldCount(); i++) {
					((ThumbnailField)((CellContainer)((RowContainer)getField(i)).getField(0)).getContent()).setSelected(false);
				}
				isSelecting = false;
				invalidate();
    		}
    	}
    	
    	// ******************** PAINTING AND BORDRERS **************
    	protected void paint(Graphics g) {
    		super.paint(g);
    		if(isSelecting) {
    			drawBorder(getFieldWithFocus(), Themes.getCurrentTheme().getBorderSelectColor());
    		} else if(AlbumViewScreen.isMovingImage) {
    			drawBorder(view.getFieldWithFocus(), Themes.getCurrentTheme().getBorderMoveColor());
    		} else {
    			drawBorder(view.getFieldWithFocus(), Themes.getCurrentTheme().getBorderFocusColor());
    		}
    	}
    	
    	public void drawBorder(Field f, int color) {
    		if(f != null) {
    			Graphics g = getGraphics0();
    			g.setColor(color);
    			int yOffset = f.getExtent().y;
    			XYRect cell = ((RowContainer)f).getField(0).getExtent();
    			
    			g.drawRect(cell.x, cell.y+yOffset, cell.width, cell.height);
    			g.drawRect(cell.x-1, (cell.y+yOffset)-1, cell.width+2, cell.height+2);
    		}
		}    
    	
    	// ************************* TRACKWHEEL HANDLER **********************
    	protected int moveFocus(int amount, int status, int time) {
    		if(!AlbumViewScreen.isMovingImage &&
    				((status & KeypadListener.STATUS_ALT) == KeypadListener.STATUS_ALT ||
    				(status & KeypadListener.STATUS_SHIFT) == KeypadListener.STATUS_SHIFT)) {
    			isSelecting = true;
    			if(amount < 0) { amount = -1; } else { amount = 1; }
    			int idx = getFieldWithFocusIndex()+amount;
    			if(idx >= 0 && idx < getFieldCount()) {
    				getField(idx).setFocus();
    				((ThumbnailField)getLeafFieldWithFocus()).setSelected(true);
        			((CellContainer)((RowContainer)getFieldWithFocus()).getField(0)).invalidate();
    			}
    			return 0;
    		}
    		clearSelected();
    		return super.moveFocus(amount, status, time);
    	}
    	
    	// ********************** KEYSTROKE HANDLER *********************
    	protected boolean keyStatus(int keycode, int time) {
    		if(!AlbumViewScreen.isMovingImage &&
    				((Keypad.status(keycode) & KeyListener.STATUS_ALT) == KeyListener.STATUS_ALT ||
    				(Keypad.status(keycode) & KeyListener.STATUS_SHIFT) == KeyListener.STATUS_SHIFT)) {
    			isSelecting = true;
    			((ThumbnailField)getLeafFieldWithFocus()).setSelected(true);
    			((RowContainer)getFieldWithFocus()).invalidate();
    			return true;
    		}
    		clearSelected();
    		return super.keyStatus(keycode, time);
    	}
    }

	public void run() {
		alive = true;
		int size = album.getSize();
		for(int i = 0; i < size; i++) {
        	RowContainer row = new RowContainer();
        		ThumbnailField f = new ThumbnailField(album.getPicture(i));
        		DetailField d = new DetailField(album.getPicture(i));
        	row.add(f);
        	row.add(d);
        	synchronized(BBSmartPhoto.getEventLock()) {
        		view.add(row);
        	}
        }
		alive = false;
	}
	
	public boolean isAlive() {return alive; }
	
	public boolean isSelecting() { return isSelecting; }
	
	// *********************** FIELD CONTAINERS **********************
	private class RowContainer extends HorizontalFieldManager implements FocusChangeListener {
    	public RowContainer() {
    		super(HorizontalFieldManager.USE_ALL_WIDTH);
    		setFocusListener(this);
    	}
    	
    	protected void paint(Graphics g) {
    		super.paint(g);
    		drawBorder(g);
    	}
    	
    	private void drawBorder(Graphics g) {
    		g.setColor(Themes.getCurrentTheme().getBorderUnfocusColor());
    		g.drawRect(0, 0, getWidth(), getHeight()+1);
    		
    		Field f = this.getField(0);
    		int x = f.getLeft()+f.getWidth();
    		g.drawLine(x, 0, x, getHeight());
    	}
    	
    	public void add(Field f) {
    		CellContainer cell = new CellContainer();
    		ContentContainer content = new ContentContainer();
    		content.add(f);
    		content.setMargin(1, 1, 1, 1);
    		content.setWidth(f.getPreferredWidth());
    		cell.add(content);
    		cell.setWidth(f.getPreferredWidth());
    		super.add(cell);
    	}

    	public void focusChanged(Field f, int eventType) {
    		if(eventType == FOCUS_GAINED) {
    			if(isSelecting) {
    				((CellContainer)getField(0)).invalidate();
    				view.drawBorder(this, Themes.getCurrentTheme().getBorderSelectColor());
    			} else if(AlbumViewScreen.isMovingImage) {
    				view.drawBorder(this, Themes.getCurrentTheme().getBorderMoveColor());
    			} else {
    				view.drawBorder(this, Themes.getCurrentTheme().getBorderFocusColor());
    			}
    		} else if(eventType == FOCUS_LOST) {
    			invalidate();
    		}
    	}
    }
	
	private class CellContainer extends HorizontalFieldManager {
    	private int width;
    	public CellContainer() {
    		super();
    		setMargin(1, 1, 1, 1);
    	}
		public void setWidth(int aWidth) { width = aWidth; }
		public Field getContent() {
			return ((ContentContainer)this.getField(0)).getField(0);
		}
		protected void sublayout(int aWidth, int aHeight) {
			super.sublayout(width, aHeight);
		}
    }

	private class ContentContainer extends HorizontalFieldManager {
    	private int width;
		public void setWidth(int aWidth) { width = aWidth; }
		protected void sublayout(int aWidth, int aHeight) {
			aWidth = width - (getMarginLeft()+getMarginRight());
			super.sublayout(aWidth, aHeight);
			XYRect extent = getExtent();
			extent.setSize(aWidth, extent.height);
		}
    }
	
	// **************** INHERETED METHODS FROM BBPHOTOVIEW **********************
	public Field getAsField() {
		return view;
	}

	public void deletePicture(String path) {
		for(int i = 0; i < view.getFieldCount(); i++) {
			RowContainer row = (RowContainer)view.getField(i);
			if(((ThumbnailField)((CellContainer)row.getField(0)).getContent()).getAlbumPicture().equals(path)) {
				view.delete(row);
				return;
			}
		}
	}
	
	public void moveFocusedPicture(int dx, final int dy) {
		RowContainer row = (RowContainer)view.getFieldWithFocus();
        ThumbnailField f11 = (ThumbnailField)((CellContainer)row.getField(0)).getContent();
        DetailField f12 = (DetailField)((CellContainer)row.getField(1)).getContent();

        int idx = row.getIndex();
        idx = idx+dx+dy;
        if(idx < 0) { return; }
        if(idx >= view.getFieldCount()) { return; }
   
        RowContainer nextRow = (RowContainer)view.getField(idx);
        ThumbnailField f21 = (ThumbnailField)((CellContainer)nextRow.getField(0)).getContent();
        DetailField f22 = (DetailField)((CellContainer)nextRow.getField(1)).getContent();
        
        AlbumPicture p1 = album.getPicture(f11.getAlbumPicture());
        AlbumPicture p2 = album.getPicture(f21.getAlbumPicture());
        
        f11.updateField(p2);							
        f12.updateField(p2);
        
        f21.updateField(p1);							
        f22.updateField(p1);
        
        f21.setFocus();							

        album.swapPictures(album.getIndexOfPicture(p1.getPath()), album.getIndexOfPicture(p2.getPath()));
		AlbumViewScreen.dataValid = false;
	}
	
	public Album getAlbum() {
		return album;
	}
	
	public int getAlbumIndex() {
		return album.getIndexOfPicture(getFocusedPicture());
	}
	
	public String getFocusedPicture() {
		Field f = view.getLeafFieldWithFocus();
		if(f == null) { return null; }
		return ((ThumbnailField)f).getAlbumPicture();
	}
	
	public Vector getSelectedPictures() {
		Vector v = new Vector();
		if(isSelecting) {
			for(int i = 0; i < view.getFieldCount(); i++) {
				RowContainer row = (RowContainer)view.getField(i);
				ThumbnailField f = (ThumbnailField)((CellContainer)row.getField(0)).getContent();
				if(f.isSelecting()) {
					v.addElement(f.getAlbumPicture());
				}
			}
			view.clearSelected();
		} else {
			v.addElement(getFocusedPicture());
		}
		return v;
	}
	
	public String getNote() {
		AlbumPicture p = album.getPicture(getFocusedPicture());
		if(p == null) { return null; }
		return p.getNote();
	}
	
	public String trackImage() {
		return "(" + (view.getFieldWithFocusIndex()+1) + "/" + album.getSize() + ")";
	}
	
	// Assumes that a row consists of at least 2 cells where the cell at index 1 
	// contains a DetailField object with a defined update(String) method
	public void update(String path, String oldPath) {
		RowContainer row = (RowContainer)view.getFieldWithFocus();
		if(row == null) { return; }
    	((DetailField)((CellContainer)row.getField(1)).getContent()).updateName(path);
	}
	
	public void updateBorder() {
		((RowContainer)view.getFieldWithFocus()).focusChanged(null, FocusChangeListener.FOCUS_GAINED);
	}
}