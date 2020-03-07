package com.bbsmart.pda.blackberry.bbphoto.ui.screens;

import com.bbsmart.pda.blackberry.bbphoto.BBSmartPhoto;

import net.rim.blackberry.api.browser.Browser;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;

import com.bbsmart.pda.blackberry.bbphoto.ui.customfields.*;
import com.bbsmart.pda.blackberry.bbphoto.io.*;
import com.bbsmart.pda.blackberry.bbphoto.util.ImageFileUtil;
import com.bbsmart.pda.blackberry.bbphoto.util.UiUtil;
import com.bbsmart.pda.blackberry.bbphoto.util.rpn.*;

public class RegisterScreen extends MainScreen {
	private boolean minimizeOnClose;

	private String buyNowURL = "https://www.mobihand.com/mobilecart/mc1.asp?posid=16&pid=19687&did="
			+ Integer.toHexString(DeviceInfo.getDeviceId()).toUpperCase();

	private StyledButtonField registerBtn;
	private BasicEditField keyField;

	public RegisterScreen(boolean minimizeOnClose) {
		this.minimizeOnClose = minimizeOnClose;

		setScreenFont();
		initWidgets();
		initDisplay();
	}

	private void setScreenFont() {
		setFont(getFont().derive(Font.BOLD, 17, Ui.UNITS_px,
				Font.ANTIALIAS_STANDARD, 0));
	}

	private void initWidgets() {
		keyField = new HighlightedEditField("Registration Key: ", "", 10,
				BasicEditField.FILTER_NUMERIC);

		registerBtn = new StyledButtonField("Register",
				StyledButtonField.SIZE_MID, 0, getFont());
		registerBtn.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field field, int context) {
				validateInputKey();
			}
		});
	}

	private void validateInputKey() {
		if (keyField.getText().trim().length() == 0) {
			Dialog
					.alert("Please enter your registration key in the \"Key\" field");
			return;
		}

		if (activateProduct(keyField.getText())) {
			TrialManager tMan = TrialManager.getInstance();

			// Register the application and save the registration key
			tMan.state = TrialManager.STATE_REG;
			tMan.activationKey = keyField.getText();
			tMan.save();

			Dialog
					.alert("Thank you for purchasing the full version of BBSmart Photo Pro!");
			onClose();
		} else {
			Dialog
					.alert("Invalid Key Entered.\nIf you have purchased a valid key, please contact support@blackberrysmart.com for assistance");
		}
	}

	public boolean activateProduct(String key) {
		String basePIN = Integer.toHexString(DeviceInfo.getDeviceId());
		String devicePINUpper = basePIN.toUpperCase();
		String devicePINLower = basePIN.toLowerCase();

		boolean success = false;
		String out = RPNString.apply("key 7 * c +", devicePINUpper);
		success = out.equals(key);

		if (success) {
			return true;
		} else {
			// Uppercase device PIN didn't work - let's try lowercase too just
			// in case
			out = RPNString.apply("key 7 * c +", devicePINLower);
			return out.equals(key);
		}
	}

	private void initDisplay() {
		setTitle();

		heading("> REGISTRATION", true);
		text("To register the full version of BBSmart Photo Pro, please enter your product Registration Key (sometimes also called the Activation Code) below and click \"Register\"\n");

		add(keyField);

		HorizontalFieldManager hfm = new HorizontalFieldManager(
				HorizontalFieldManager.FIELD_HCENTER);
		hfm.add(registerBtn);
		add(hfm);

		spacer();
		heading("> DON'T HAVE A KEY?", true);
		text("If you do not have a registration key, you can purchase one either of the following ways:\n");

		heading(">> From your PC", false);
		text("From your home PC, head on over to the BBSmart website and pick up a copy of BBSmart Photo Pro.");
		link("www.blackberrysmart.com", "http://www.blackberrysmart.com");

		spacer();

		heading(">> From your BlackBerry", false);
		text("Alternatively you can buy online right now from our secure mobile-friendly store by clicking the \"Buy Now\" link below.");
		link("Buy Now", buyNowURL);
	}
	
	private void setTitle() {
		Bitmap back = ImageFileUtil.getThemeImage(UiUtil.getTitle());
		Bitmap text = ImageFileUtil.getTitleTextImage("title_register.png");

		Graphics g = new Graphics(back);
		g.drawBitmap((back.getWidth() / 2) - (text.getWidth() / 2), (back
				.getHeight() / 2)
				- (text.getHeight() / 2), text.getWidth(), text.getHeight(),
				text, 0, 0);
		super.setTitle(new BitmapField(back));
	}

	private void heading(String headingText, boolean major) {
		add(new ColorLabelField(headingText, major ? Color.BLUE : Color.GREEN));
	}

	private void text(String text) {
		add(new BasicEditField("", text.toString(),
				BasicEditField.DEFAULT_MAXCHARS, BasicEditField.READONLY));
	}

	private void link(String show, final String url) {
		HrefField siteLink = new HrefField(show, false, getFont());
		siteLink.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field field, int context) {
				Browser.getDefaultSession().displayPage(url);
			}
		});

		HorizontalFieldManager hfm = new HorizontalFieldManager(
				HorizontalFieldManager.FIELD_HCENTER);
		hfm.add(siteLink);
		add(hfm);
	}

	private void spacer() {
		add(new LabelField("", LabelField.READONLY));
	}

	public boolean onClose() {
		if (minimizeOnClose) {
			close();
			BBSmartPhoto.instance.requestBackground();
		} else {
			close();
		}

		return true;
	}
}

class HighlightedEditField extends BasicEditField {
	public HighlightedEditField(String label, String initialValue,
			int maxNumChars, long style) {
		super(label, initialValue, maxNumChars, style
				| BasicEditField.NO_NEWLINE);
	}

	protected void drawFocus(net.rim.device.api.ui.Graphics graphics, boolean on) {
		super.drawFocus(graphics, on);

		XYRect r = graphics.getClippingRect();

		if (on) {
			graphics.drawRect(r.x, r.y, r.width - r.x, r.height);
		} else {
			int base = graphics.getColor();
			graphics.setColor(0xFFFFFF);
			graphics.drawRect(r.x, r.y, r.width - r.x, r.height);
			graphics.setColor(base);
		}

		invalidate();
	}

	protected void paint(Graphics graphics) {
		graphics.clear(graphics.getClippingRect());
		super.paint(graphics);
	}
}
