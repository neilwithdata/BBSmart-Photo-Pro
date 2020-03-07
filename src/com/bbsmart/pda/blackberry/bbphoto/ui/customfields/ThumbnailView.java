package com.bbsmart.pda.blackberry.bbphoto.ui.customfields;

import java.util.Vector;

import net.rim.device.api.system.Display;
import net.rim.device.api.system.KeyListener;
import net.rim.device.api.system.KeypadListener;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.container.FlowFieldManager;
import net.rim.device.api.ui.container.HorizontalFieldManager;

import com.bbsmart.pda.blackberry.bbphoto.BBSmartPhoto;
import com.bbsmart.pda.blackberry.bbphoto.models.Album;
import com.bbsmart.pda.blackberry.bbphoto.models.AlbumPicture;
import com.bbsmart.pda.blackberry.bbphoto.ui.screens.AlbumViewScreen;
import com.bbsmart.pda.blackberry.bbphoto.util.Themes;

public final class ThumbnailView implements BBPhotoView {
	private ThumbnailManager view;
	private Album album;
	private boolean alive = false;
	private boolean isSelecting = false;
	
	private int borderWidth = 1;
    private int cols = Display.getWidth()/80;
    private int[] cellWidths = new int[cols];
	
	public ThumbnailView(Album a) {
		album = a;
		view = new ThumbnailManager(ThumbnailManager.HORIZONTAL_SCROLL | ThumbnailManager.VERTICAL_SCROLL);
		
		int totalBorderPixelWidth = (borderWidth * cols) + borderWidth;
		int maxContentPixelWidth = Display.getWidth() - totalBorderPixelWidth;
		
		int pixelsLeft = maxContentPixelWidth;
		for (int i = 0; i < cols; i++) {
			if (i == cols - 1) {
				cellWidths[i] = pixelsLeft;
			} else {
				int nextPercent = 100/cols;
				/** now multiply to get the number of pixels */
				double multiplier = ((double) nextPercent) * ((double) 0.01);
				double result = multiplier * (double) maxContentPixelWidth;
				int baseLine = (int) result;

				int adder = (result % baseLine > 0) ? 1 : 0;

				baseLine += adder;

				pixelsLeft -= baseLine;
				cellWidths[i] = baseLine;
			}
		}
	}

	private final class ThumbnailManager extends FlowFieldManager {
		public ThumbnailManager(long style) {
    		super(style);
    	}
		
		private void clearSelected() {
			if(isSelecting) {
				for(int i = 0; i < this.getFieldCount(); i++) {
					((ThumbnailField)((CellContainer)getField(i)).getContent()).setSelected(false);
				}
				isSelecting = false;
				invalidate();
			}
		}
		
		// ******************* SIZE AND LAYOUT *********************
		// TODO: See if the +4 can be removed
    	protected void sublayout(int aWidth, int aHeight) {
    		super.sublayout(Display.getWidth()+4, aHeight);
    	}
    	
    	public int getPreferredWidth() {
    		return Display.getWidth()+4;
    	}
    	
    	// ******************** PAINTING AND BORDRERS **************
    	protected void paint(Graphics g) {
    		super.paint(g);
    		if(isSelecting) {
    			drawBorder(getFieldWithFocus(), Themes.getCurrentTheme().getBorderSelectColor());
    		} else if(AlbumViewScreen.isMovingImage) {
    			drawBorder(getFieldWithFocus(), Themes.getCurrentTheme().getBorderMoveColor());
    		} else {
    			drawBorder(getFieldWithFocus(), Themes.getCurrentTheme().getBorderFocusColor());
    		}
    	}
    	
    	// FIXME: When the field contains a number of fields too small to require scrolling the graphics context is clipped
    	public void drawBorder(Field f, int color) {
    		if(f != null) {
    			Graphics g = getGraphics0();
    			g.setColor(color);
    			
    			XYRect rect = f.getExtent();
    			
    			g.drawRect(rect.x, rect.y, rect.width, rect.height);
    			g.drawRect(rect.x-1, rect.y-1, rect.width+2, rect.height+2);
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
    				((CellContainer)getFieldWithFocus()).invalidate();
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
    			((CellContainer)getFieldWithFocus()).invalidate();
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
        	CellContainer cell = new CellContainer(); 
        		ContentContainer content = new ContentContainer();
        			ThumbnailField f = new ThumbnailField(album.getPicture(i));
    			content.add(f);
    			content.setMargin(1, 1, 1, 1);
    			content.setWidth(cellWidths[i%cols]);
            cell.add(content);
            cell.setWidth(cellWidths[i%cols]);
            synchronized(BBSmartPhoto.getEventLock()) {
            	view.add(cell);
            }
        }
        alive = false;
	}
	
	public boolean isAlive() { return alive; }
	
	public boolean isSelecting() { return isSelecting; }
	
	// *********************** FIELD CONTAINERS **********************
    private class CellContainer extends HorizontalFieldManager implements FocusChangeListener {
    	private int width;
    	public CellContainer() {
    		super();
    		setMargin(borderWidth, borderWidth, borderWidth, borderWidth);
    		setFocusListener(this);
    	}
		public void setWidth(int aWidth) { width = aWidth; }
		public Field getContent() {
			return ((ContentContainer)this.getField(0)).getField(0);
		}
		protected void sublayout(int aWidth, int aHeight) {
			super.sublayout(width, aHeight);
		}
		
		public void focusChanged(Field f, int eventType) {
    		if(eventType == FOCUS_GAINED) {
    			if(isSelecting) {
    				invalidate();
    				view.drawBorder(this, Themes.getCurrentTheme().getBorderSelectColor());
    			} else if(AlbumViewScreen.isMovingImage) {
    				view.drawBorder(this, Themes.getCurrentTheme().getBorderMoveColor());
    			} else {
    				view.drawBorder(this, Themes.getCurrentTheme().getBorderFocusColor());
    			}
    		} else if(eventType == FOCUS_LOST) {
    			view.drawBorder(this, Graphics.FULL_WHITE);
    		}
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
			CellContainer f = (CellContainer)view.getField(i);
			if(((ThumbnailField)f.getContent()).getAlbumPicture().equals(path)) {
				view.delete(f);
				return;
			}
		}
	}
	
	public void moveFocusedPicture(int dx, int dy){
		CellContainer cell = (CellContainer)view.getFieldWithFocus();
        ThumbnailField f1 = (ThumbnailField)cell.getContent();

        int idx = cell.getIndex();
        idx = idx+dx+dy;
        if(idx < 0) { return; }
        if(idx >= view.getFieldCount()) { return; }
   
        CellContainer nextCell = (CellContainer)view.getField(idx);
        ThumbnailField f2 = (ThumbnailField)nextCell.getContent();
        
        String s1 = f1.getAlbumPicture();
        String s2 = f2.getAlbumPicture();
        
        f1.updateField(album.getPicture(s2));		// Paints this field
        f2.updateField(album.getPicture(s1));		// Paints this field
        
        f2.setFocus();								// Paints field that lost focus eg. f1

        album.swapPictures(album.getIndexOfPicture(s1), album.getIndexOfPicture(s2));
		AlbumViewScreen.dataValid = false;
	}
	
	public Album getAlbum() {
		return album;
	}
	
	public int getAlbumIndex() {
		return album.getIndexOfPicture(getFocusedPicture());
	}
	
	public String getNote() {
		AlbumPicture p = album.getPicture(getFocusedPicture());
		if(p == null) {	return null; }
		return p.getNote();
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
				ThumbnailField f = (ThumbnailField)((CellContainer)view.getField(i)).getContent();
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
	
	public String trackImage() {
		CellContainer cell = (CellContainer)view.getFieldWithFocus();
    	String file = ((ThumbnailField)cell.getContent()).getAlbumPicture();

        file = file.substring(file.lastIndexOf('/')+1, file.lastIndexOf('.'));
        return file + " (" + (cell.getIndex()+1) + "/" + album.getSize() + ")";
	}
	
	public void update(String path, String oldPath) {
	}
	
	public void updateBorder() {
		((CellContainer)view.getFieldWithFocus()).focusChanged(null, FocusChangeListener.FOCUS_GAINED);
	}
}