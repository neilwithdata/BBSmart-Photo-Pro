package com.bbsmart.pda.blackberry.bbphoto.ui.customfields;

import com.bbsmart.pda.blackberry.bbphoto.util.ImageFileUtil;
import com.bbsmart.pda.blackberry.bbphoto.util.UiUtil;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;

public final class OptionsChoiceField extends Field {
	private int fieldWidth;
	private int fieldHeight;
	
	private final String[] labels = {UiUtil.OPTIONS_SLIDESHOW_TITLE, UiUtil.OPTIONS_SCNSAVER_TITLE, UiUtil.OPTIONS_GENERAL_TITLE};
	private final String[] images = {"slideshow", "scrnsave", "general"};
	private int selectedIdx = 0;
	private final int maxIdx = 2;
	private final Bitmap[] itemImages;
	
	private FieldChangeListener[] itemListeners;	
	
	public static final int ITEM_SLIDESHOW = 0;
	public static final int ITEM_SCREENSAVER = 1;
	public static final int ITEM_GENERAL= 2;
	
	public OptionsChoiceField() {
		super();
		itemListeners = new FieldChangeListener[maxIdx + 1];
		itemImages = new Bitmap[images.length * 2];
		
		int i;
		for (i = 0; i < images.length; i++) {
			itemImages[i * 2] = Bitmap.getBitmapResource("img/" + images[i] + ".png");
			itemImages[i * 2 + 1] = Bitmap.getBitmapResource("img/" + images[i] + "-roll.png");
		}
	}
	
	protected void layout(int availWidth, int availHeight) {
		fieldWidth = availWidth;
		fieldHeight = 130;
		setExtent(fieldWidth, fieldHeight);
	}

	private void drawItem(Graphics g, int x, int y, int diameter, int index) {
		boolean selected = index == selectedIdx;
		if (!selected) {
			g.drawBitmap(x - diameter/2, y - diameter/2, diameter, diameter, itemImages[index * 2], 0, 0);
		} else {
			g.drawBitmap(x - diameter/2, y - diameter/2, diameter, diameter, itemImages[index * 2 + 1], 0, 0);
		}
	}
	
	protected void paint(Graphics g) {
		final int dia = 56;
		final int y1 = dia/2 + 20;
		final int xsep = 80;
		
		// 3 across the top
		drawItem(g, fieldWidth/2 - xsep, y1, dia, 0);
		drawItem(g, fieldWidth/2, y1, dia, 1);
		drawItem(g, fieldWidth/2 + xsep, y1, dia, 2);
				
		// status line
		if (selectedIdx >= 0 && selectedIdx <= maxIdx) {
			Bitmap text = ImageFileUtil.getTitleTextImage(labels[selectedIdx]);
			g.drawBitmap((fieldWidth/2)-(text.getWidth()/2), fieldHeight-text.getHeight(), 
					text.getWidth(), text.getHeight(), text, 0, 0);
		}
	}
	
	public boolean isFocusable(){
		return true;
	}
	
	protected void drawFocus(Graphics graphics, boolean on) {
	}
	
	protected void onFocus(int direction){
		if (direction >= 0){
			// Focus the first item
			selectedIdx = 0;
		}else{
			// focus the last item
			selectedIdx = maxIdx;
		}
		invalidate();
	}
	
	protected void onUnfocus() {
		selectedIdx = -1;
		invalidate();
	}
	
	protected int moveFocus(int amount, int status, int time){
		int ret = 0;
		int newSelect = selectedIdx;
		
		newSelect += amount;
		
		if (amount > 0) {
			// move focus forwards
			if (newSelect > maxIdx){
				ret = newSelect - maxIdx;
				newSelect = maxIdx;
			}
		} else if (amount < 0) {
			if (newSelect < 0){
				// Never lose focus off the top of the field
				ret = 0;
				newSelect = 0;
			}
		}
		
		if (newSelect != selectedIdx)
		{
			selectedIdx = newSelect;
			invalidate();
		}
		
		return ret;
	}
	
	protected boolean trackwheelClick(int status, int time){
		runSelectedItem();
		return true;
	}
	
	protected boolean navigationMovement(int dx, int dy, int status, int time) {
		if (selectedIdx >= 0) {
			if (selectedIdx == 0) {
				if (dy < 0 || dx < 0) {
					// Focus is on first element and up move attempted - swallow
					return true;
				}
			} else {
				if(selectedIdx == maxIdx) {
					if (dy > 0 || dx > 0) {
						// focus is on last element and attempted to move down -
						// swallow
						return true;
					}
				}
			}
		}
		return super.navigationMovement(dx, dy, status, time);
	}
	
	protected boolean keyDown(int keycode, int time){
		switch(Keypad.key(keycode)) {
			case Keypad.KEY_SPACE:
			case Keypad.KEY_ENTER:
				runSelectedItem();
				return true;
			default:
				return false;
		}
	}
	
	public void setChangeListener(int item, FieldChangeListener listener) {
		if (item >= 0 && item <= maxIdx && itemListeners != null) {
			itemListeners[item] = listener;
		}
	}

	public void runSelectedItem() {
		if (selectedIdx >= 0 && selectedIdx <= maxIdx && itemListeners != null && itemListeners[selectedIdx] != null) {
			itemListeners[selectedIdx].fieldChanged(this, selectedIdx);
		}
	}
}