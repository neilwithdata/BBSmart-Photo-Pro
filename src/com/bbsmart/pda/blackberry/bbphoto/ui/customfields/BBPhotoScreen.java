package com.bbsmart.pda.blackberry.bbphoto.ui.customfields;

import com.bbsmart.pda.blackberry.bbphoto.util.ImageFileUtil;
import com.bbsmart.pda.blackberry.bbphoto.util.UiUtil;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.FontFamily;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.container.MainScreen;
import net.rim.device.api.ui.container.VerticalFieldManager;

public class BBPhotoScreen extends MainScreen {
	private VerticalFieldManager mainManager;
	
	private String titleImage;
	private BitmapField titleField;
	
	public BBPhotoScreen() {
		this(MainScreen.NO_VERTICAL_SCROLL);
		mainManager = new VerticalFieldManager(VerticalFieldManager.USE_ALL_WIDTH | VerticalFieldManager.USE_ALL_HEIGHT | 
								VerticalFieldManager.NO_VERTICAL_SCROLL) {
			private Bitmap background;
			protected void sublayout(int width, int height) {
				background = ImageFileUtil.getThemeImage(UiUtil.getScreenBackground());
				super.sublayout(width, height);
			}
			protected void paint(Graphics g) {
				g.drawBitmap(0, 0, getWidth(), getHeight(), background, 0, 0);
				super.paint(g);
			}
		};
		super.add(mainManager);
	}
	
	public BBPhotoScreen(long style) {
		super(style);
		setScreenFont();
	}
	
	public void add(Field field) {
		if(mainManager != null) {
			field.setPadding(1, 2, 0, 5);
			mainManager.add(field);
		} else {
			super.add(field);
		}
	}
	
	public void setTitle(String title) {
		titleImage = title;
		Bitmap back = ImageFileUtil.getThemeImage(UiUtil.getTitle());
		Bitmap text = ImageFileUtil.getTitleTextImage(title);
		Graphics g = new Graphics(back);
		g.drawBitmap((back.getWidth()/2)-(text.getWidth()/2), (back.getHeight()/2)-(text.getHeight()/2), 
				text.getWidth(), text.getHeight(), text, 0, 0);
		titleField = new BitmapField(back);
		
		super.insert(titleField, 0);
		super.insert(new BitmapField(UiUtil.SEPARATOR), 1);
	}
	
	protected void sublayout(int width, int height) {
		if(titleField != null) { 
			Bitmap back = ImageFileUtil.getThemeImage(UiUtil.getTitle());
			Bitmap text = ImageFileUtil.getTitleTextImage(titleImage);
			Graphics g = new Graphics(back);
			g.drawBitmap((back.getWidth()/2)-(text.getWidth()/2), (back.getHeight()/2)-(text.getHeight()/2), 
					text.getWidth(), text.getHeight(), text, 0, 0);
			titleField.setBitmap(back);
		}
		super.sublayout(width, height);
	}
	
	private void setScreenFont() {
        Font f;
        try {
            f = FontFamily.forName("BBMillbankTall").getFont(Font.BOLD, 17);
            f = f.derive(f.getStyle(), f.getHeight(), Ui.UNITS_px,
                    Font.ANTIALIAS_STANDARD, 0);
        } catch (ClassNotFoundException cnfe) {
            f = getFont().derive(Font.BOLD, 17, Ui.UNITS_px,
                    Font.ANTIALIAS_STANDARD, 0);
        }
        setFont(f);
    }
	
	// ******************** KEYSTROKE HANDLER ****************
	protected boolean keyDown(int keycode, int time) {
		switch(Keypad.key(keycode)) {
			case Keypad.KEY_END:
				onClose();
				break;
			default:
				return super.keyDown(keycode, time);
		}
		return true;
	}
}