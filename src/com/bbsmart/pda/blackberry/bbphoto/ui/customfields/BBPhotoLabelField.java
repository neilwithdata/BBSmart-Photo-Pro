package com.bbsmart.pda.blackberry.bbphoto.ui.customfields;

import com.bbsmart.pda.blackberry.bbphoto.util.ImageFileUtil;
import com.bbsmart.pda.blackberry.bbphoto.util.Themes;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;

public class BBPhotoLabelField extends Field implements DrawStyle {
	private int TEXT_FOCUS_COLOR = Themes.getCurrentTheme().getTextFocusColor();
	private int TEXT_UNFOCUS_COLOR = Themes.getCurrentTheme().getTextUnfocusColor();
	
	private String text;
	private String backgrdFile;
	private Bitmap background;
	
	// ******************* CONSTRUCTORS ************************
	public BBPhotoLabelField() {
		super();
	}
	
	public BBPhotoLabelField(String text) {
		this();
		this.text = text.toString();
	}

	public BBPhotoLabelField(String text, long style) {
		super(style);
		this.text = text.toString();
	}
	
	public BBPhotoLabelField(String text, String b) {
		this(text);
		backgrdFile = b;
	}
	
	public BBPhotoLabelField(String text, String b, long style) {
		this(text, style);
		backgrdFile = b;
	}
	
	// ********************** METHODS **********************
	public void setBackground(String b) {
		backgrdFile = b;
	}
	
	public String getText() {
		return text;
	}
	
	public Bitmap getBackground() {
		return background;
	}
	
	public void setText(Object text) {
		this.text = text.toString();
		layout(0,0);
		invalidate();
	}
	
	public void setColors(int unFocused, int focused) {
		TEXT_UNFOCUS_COLOR = unFocused;
		TEXT_FOCUS_COLOR = focused;
	}
	
	public void updateTheme() {
		TEXT_FOCUS_COLOR = Themes.getCurrentTheme().getTextFocusColor();
		TEXT_UNFOCUS_COLOR = Themes.getCurrentTheme().getTextUnfocusColor();
	}
	
	// ******************* PAINTING AND LAYOUT ***************
	protected void layout(int width, int height) {
		if(backgrdFile != null) { background = ImageFileUtil.getThemeImage(backgrdFile); }
		// Use the greater of text advance or background image width; then clip to screen width
		int fieldWidth = this.getFont().derive(Font.BOLD).getAdvance(text) + 8;
		if(background != null) { if(background.getWidth() > fieldWidth) { fieldWidth = background.getWidth(); } }
		if(fieldWidth > Graphics.getScreenWidth()) { fieldWidth = Graphics.getScreenWidth(); }
		// Use the greater of font height or background image height
		int fieldHeight = this.getFont().derive(Font.BOLD).getHeight();
		if(background != null) { if(background.getHeight() > fieldHeight) { fieldHeight = background.getHeight(); } }
		setExtent(fieldWidth, fieldHeight);
	}
	
	protected void paint(Graphics g) {
		if(isVisible()) {
			if(background != null) {
				int x = (getWidth()/2) - (background.getWidth()/2); if(x < 0) { x = 0; }
				int y = (getHeight()/2) - (background.getHeight()/2); if (y < 0) { y = 0; }
				g.drawBitmap(x, y, background.getWidth(), background.getHeight(), background, 0, 0);
			}
			writeText(g, TEXT_UNFOCUS_COLOR);
		}
	}
	
	protected void drawFocus(Graphics g, boolean on) {
		if(on) {
			writeText(g, TEXT_FOCUS_COLOR);
		} 
	}
	
	protected void writeText(Graphics g, int color) {
		g.setFont(g.getFont().derive(Font.BOLD));
		g.setGlobalAlpha(255);
		g.setColor(color);
		int y = (getHeight()/2) - (g.getFont().getHeight()/2);
		if(isStyle(HCENTER)) {
			g.setDrawingStyle(HCENTER, true);
			g.drawText(text, (getWidth()/2)-((g.getFont().getAdvance(text))/2), y);
		} else {
			g.drawText(text, 0, y);
		}
	}
}