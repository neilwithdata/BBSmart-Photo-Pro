package com.bbsmart.pda.blackberry.bbphoto.ui.customfields;

import com.bbsmart.pda.blackberry.bbphoto.util.ImageFileUtil;
import com.bbsmart.pda.blackberry.bbphoto.util.UiUtil;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.ContextMenu;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.MenuItem;

public class StyledButtonField extends Field {
	public static final int SIZE_SMALL = 0;
	public static final int SIZE_MID = 1;
	public static final int SIZE_LARGE = 2;

	private Font textFont;
	private int textOnColor;
	private int textOffColor;

	private boolean isCalculateWidth;
	private int numMids;

	private int btnWidth;
	private int btnHeight;

	private Bitmap left;
	private Bitmap mid;
	private Bitmap right;
	private int leftWidth;
	private int midWidth;
	private int rightWidth;

	private boolean hasFocus;
	
	// When strict menu is true, on a trackwheel blackberry it will always
	// throw up the menu on trackwheelclick
	private boolean strictMenu;

	private String text;
	private int size;

	public StyledButtonField(String text, int size, int btnWidth, Font textFont) {
		super(Field.FOCUSABLE | Field.FIELD_VCENTER);

		this.text = text;
		this.size = size;
		this.textFont = textFont;
		
		this.strictMenu = false;

		initElements();

		if (btnWidth == 0) {
			isCalculateWidth = true;
			calculateNumMids();
			this.btnWidth = leftWidth + (midWidth * numMids) + rightWidth;
		} else {
			isCalculateWidth = false;
			this.btnWidth = btnWidth;
			calculateNumMids();
		}
	}

	private void initElements() {
		loadBtnBitmaps();

		textOffColor = Color.WHITE;
		textOnColor = Color.YELLOW;

		hasFocus = false;
	}

	public void setTextOnColor(int color) {
		this.textOnColor = color;
	}

	public void setTextOffColor(int color) {
		this.textOffColor = color;
	}
	
	public void setStrictMenu(boolean strictMenu) {
		this.strictMenu = strictMenu;
	}

	/**
	 * Returns the minimum number of 'mid' bitmap sections that need to be
	 * included to accommodate fully for the text
	 * 
	 * @return
	 */
	private void calculateNumMids() {
		numMids = 0;

		if (isCalculateWidth) {
			// Calculate the width of the button based on the text
			int textWidth = textFont.getAdvance(text);
			numMids = (int) (Math.ceil((double) textWidth / (double) midWidth));
		} else {
			int trueWidth = leftWidth + rightWidth;
			while (trueWidth < btnWidth) {
				numMids++;
				trueWidth += midWidth;
			}

			// The width of the button doesn't end up being exactly as requested
			// due to the discrete nature of the mid field but it is close
			btnWidth = trueWidth;
		}
	}

	private void loadBtnBitmaps() {
		switch (size) {
		case SIZE_LARGE:
			left = ImageFileUtil.getResourceImage("large_left_btn.png");
			mid = ImageFileUtil.getResourceImage("large_mid_btn.png");
			right = ImageFileUtil.getResourceImage("large_right_btn.png");
			break;
		case SIZE_MID:
			left = ImageFileUtil.getResourceImage("mid_left_btn.png");
			mid = ImageFileUtil.getResourceImage("mid_mid_btn.png");
			right = ImageFileUtil.getResourceImage("mid_right_btn.png");
			break;
		}

		leftWidth = left.getWidth();
		midWidth = mid.getWidth();
		rightWidth = right.getWidth();
		btnHeight = mid.getHeight();
	}

	protected void layout(int width, int height) {
		setExtent(getPreferredWidth(), getPreferredHeight());
	}
	
	public int getPreferredWidth() {
		return btnWidth;
	}

	public int getPreferredHeight() {
		return btnHeight;
	}

	protected void onFocus(int direction) {
		hasFocus = true;
		invalidate();
	}

	protected void onUnfocus() {
		hasFocus = false;
		invalidate();
	}

	protected void drawFocus(Graphics graphics, boolean on) {
		// we handle drawing our own focus...do nothing
	}
	
	protected void makeContextMenu(ContextMenu contextMenu) {
		contextMenu.addItem(new MenuItem("Select", 0, 0) {
			public void run() {
				fieldChangeNotify(1);
			}
		});
	}

	protected boolean trackwheelClick(int status, int time) {
		if (UiUtil.HAS_TRACKBALL) {
			fieldChangeNotify(1);
			return true;
		} else {
			if (!strictMenu) {
				// handle just like a regular click
				fieldChangeNotify(1);
				return true;
			}
		}

		return super.trackwheelClick(status, time);
	}

	protected void fieldChangeNotify(int context) {
		try {
			this.getChangeListener().fieldChanged(this, context);
		} catch (Exception exception) {
		}
	}
	
	protected boolean keyChar(char character, int status, int time) {
		switch (character) {
		case Keypad.KEY_ENTER:
			fieldChangeNotify(1);
			return true;
		}

		return super.keyChar(character, status, time);
	}

	protected void paint(Graphics graphics) {
		// Always draw the button HCENTER and VCENTER
		int x = 0;

		/* DRAW THE BUTTON */
		// btn left
		graphics.drawBitmap(x, 0, leftWidth, btnHeight, left, 0, 0);
		x += leftWidth;

		// btn middle
		for (int i = 0; i < numMids; i++) {
			graphics.drawBitmap(x, 0, midWidth, btnHeight, mid, 0, 0);
			x += midWidth;
		}

		// btn right
		graphics.drawBitmap(x, 0, rightWidth, btnHeight, right, 0, 0);

		/* WRITE THE TEXT */
		graphics.setColor(hasFocus ? textOnColor : textOffColor);
		graphics.setFont(textFont);

		int xIndent = (btnWidth - textFont.getAdvance(text)) / 2;
		int yIndent = (btnHeight - textFont.getHeight()) / 2;
		graphics.drawText(text, xIndent, yIndent);
	}
}