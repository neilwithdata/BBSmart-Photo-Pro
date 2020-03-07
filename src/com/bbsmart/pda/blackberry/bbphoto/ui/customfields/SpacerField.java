package com.bbsmart.pda.blackberry.bbphoto.ui.customfields;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;

public class SpacerField extends Field {
	private int fieldWidth;
	private int fieldHeight;
	private int backgroundColour = -1;

	public static final int MODE_HORIZ = 0;
	public static final int MODE_VERT = 1;

	public SpacerField() {
		super(Field.NON_FOCUSABLE);
	}
	
	public SpacerField(int mode, int len) {
		this();
		setSpace(mode, len);
	}
	
	public SpacerField(int mode, int len, int backgroundColour) {
		this(mode, len);
		this.backgroundColour = backgroundColour;
	}

	public void updateSpace(int mode, int len) {
		setSpace(mode, len);
		layout(0,0);
		invalidate();
	}
	
	private void setSpace(int mode, int len) {
		if (mode == MODE_HORIZ) { // Horizontal spacer
			this.fieldWidth = len;
			this.fieldHeight = 1;
		} else {
			this.fieldWidth = Graphics.getScreenWidth();
			this.fieldHeight = len;
		}
	}
	
	protected void layout(int width, int height) {
		setExtent(getPreferredWidth(), getPreferredHeight());
	}

	public int getPreferredWidth() {
		return fieldWidth;
	}

	public int getPreferredHeight() {
		return fieldHeight;
	}

	protected void paint(Graphics graphics) {
		if(backgroundColour > 0) {
			graphics.setColor(backgroundColour);
			graphics.fillRect(0, 0, fieldWidth, fieldHeight);
		}
	}
}
