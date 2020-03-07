package com.bbsmart.pda.blackberry.bbphoto.ui.screens;

import net.rim.blackberry.api.browser.Browser;
import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.invoke.MessageArguments;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;

import com.bbsmart.pda.blackberry.bbphoto.BBSmartPhoto;
import com.bbsmart.pda.blackberry.bbphoto.ui.customfields.*;
import com.bbsmart.pda.blackberry.bbphoto.util.ImageFileUtil;
import com.bbsmart.pda.blackberry.bbphoto.util.UiUtil;

public class TrialEndedScreen extends MainScreen {
	private String buyNowURL = "https://www.mobihand.com/mobilecart/mc1.asp?posid=16&pid=19687&did="
			+ Integer.toHexString(DeviceInfo.getDeviceId()).toUpperCase();

	private ButtonField submitFeedbackBtn;
	private ButtonField registerButton;

	public TrialEndedScreen() {
		setScreenFont();
		initButtons();
		initDisplay();
	}

	private void setScreenFont() {
		setFont(getFont().derive(Font.BOLD, 17, Ui.UNITS_px,
				Font.ANTIALIAS_STANDARD, 0));
	}

	private void initButtons() {
		submitFeedbackBtn = new ButtonField("Submit Feedback",
				ButtonField.NEVER_DIRTY | ButtonField.CONSUME_CLICK
						| ButtonField.FIELD_HCENTER);
		submitFeedbackBtn.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field field, int context) {
				MessageArguments mArgs = new MessageArguments(
						MessageArguments.ARG_NEW,
						"support@blackberrysmart.com",
						"BBSmart Photo Pro Feedback", "");

				Invoke.invokeApplication(Invoke.APP_TYPE_MESSAGES, mArgs);
			}
		});

		registerButton = new ButtonField("Register", ButtonField.CONSUME_CLICK
				| ButtonField.NEVER_DIRTY | ButtonField.FIELD_HCENTER);
		registerButton.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field field, int context) {
				BBSmartPhoto.instance.popScreen(BBSmartPhoto.instance
						.getActiveScreen());
				BBSmartPhoto.instance.pushScreen(new RegisterScreen(true));
			}
		});
	}

	private void initDisplay() {
		setTitle();
		displayText();
	}

	private void setTitle() {
		Bitmap back = ImageFileUtil.getThemeImage(UiUtil.getTitle());
		Bitmap text = ImageFileUtil.getTitleTextImage("title_trialEnded.png");

		Graphics g = new Graphics(back);
		g.drawBitmap((back.getWidth() / 2) - (text.getWidth() / 2), (back
				.getHeight() / 2)
				- (text.getHeight() / 2), text.getWidth(), text.getHeight(),
				text, 0, 0);
		super.setTitle(new BitmapField(back));
	}

	private void displayText() {
		heading("> THANKS FOR STOPPING BY", true);
		text("Your free trial of BBSmart Photo Pro, the most powerful BlackBerry photo manager, has now expired!\n");

		heading("> GET THE FULL VERSION", true);
		text("If you enjoyed using this application and now dread going back to the slow and simple media application, there is another way!\n");

		heading(">> From your PC", false);
		text("From your home PC, head on over to the BBSmart website and pick up a copy of BBSmart Photo Pro.");
		link("www.blackberrysmart.com", "http://www.blackberrysmart.com");

		spacer();

		heading(">> From your BlackBerry", false);
		text("Alternatively you can buy online right now from our secure mobile-friendly store by clicking the \"Buy Now\" link below.");
		link("Buy Now", buyNowURL);

		spacer();

		heading("> WHAT DID YOU THINK?", true);
		text("Got some feedback you would like to give? Loved it? Hated it? Got a cool idea to make it better? We'd love to hear from you!");
		add(submitFeedbackBtn);

		spacer();

		heading("> ALREADY BOUGHT IT?", true);
		text("If you have already purchased BBSmart Photo Pro, click the \"Register\" button below and on the following screen enter in your purchase registration key.");
		add(registerButton);
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
		close();
		BBSmartPhoto.instance.requestBackground();
		return true;
	}
}