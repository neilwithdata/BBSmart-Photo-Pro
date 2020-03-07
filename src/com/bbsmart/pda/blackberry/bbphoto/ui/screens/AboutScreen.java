package com.bbsmart.pda.blackberry.bbphoto.ui.screens;

import net.rim.blackberry.api.browser.Browser;
import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.invoke.MessageArguments;
import net.rim.device.api.system.DeviceInfo;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.MainScreen;

import com.bbsmart.pda.blackberry.bbphoto.*;
import com.bbsmart.pda.blackberry.bbphoto.io.*;
import com.bbsmart.pda.blackberry.bbphoto.util.*;
import com.bbsmart.pda.blackberry.bbphoto.ui.customfields.*;

public class AboutScreen extends MainScreen {
	private TrialManager tMan;

	public AboutScreen() {
		tMan = TrialManager.getInstance();

		setScreenFont();
		initDisplay();
	}

	private void setScreenFont() {
		setFont(getFont().derive(Font.BOLD, 17, Ui.UNITS_px,
				Font.ANTIALIAS_STANDARD, 0));
	}

	private void initDisplay() {
		add(new BitmapField(UiUtil.BBSMART_LOGO));

		Font btnFont = getFont().derive(0, getFont().getHeight() - 2);
		LabelField cpy = new LabelField("© BBSmart Solutions Pty Ltd",
				LabelField.FIELD_HCENTER);
		cpy.setFont(btnFont);
		add(cpy);

		HrefField siteLink = new HrefField("www.blackberrysmart.com", false,
				getFont());
		siteLink.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field field, int context) {
				Browser.getDefaultSession().displayPage(
						"http://www.blackberrysmart.com");
			}
		});

		HorizontalFieldManager hfm = new HorizontalFieldManager(
				HorizontalFieldManager.FIELD_HCENTER);
		hfm.add(siteLink);
		add(hfm);

		// Seperator
		add(new LabelField("", LabelField.NON_FOCUSABLE));

		add(new EditField("Version: ", AppInfo.VERSION_STRING,
				EditField.DEFAULT_MAXCHARS, EditField.READONLY));
		add(new EditField("Status: ", tMan.getStateString(),
				EditField.DEFAULT_MAXCHARS, EditField.READONLY));

		if (tMan.state != TrialManager.STATE_REG) {
			StyledButtonField registerButton = new StyledButtonField(
					"Buy/Register", StyledButtonField.SIZE_MID, 170, getFont());
			registerButton.setChangeListener(new FieldChangeListener() {
				public void fieldChanged(Field field, int context) {
					close();
					UiApplication.getUiApplication().pushScreen(
							new RegisterScreen(false));
				}
			});

			HorizontalFieldManager hfm1 = new HorizontalFieldManager(
					HorizontalFieldManager.FIELD_HCENTER);
			hfm1.add(registerButton);
			add(hfm1);
		}

		// Seperator
		add(new LabelField("", LabelField.NON_FOCUSABLE));

		add(new EditField("Device Type: ", DeviceInfo.getDeviceName(),
				EditField.DEFAULT_MAXCHARS, EditField.READONLY));

		add(new EditField("Device PIN: ", Integer.toHexString(
				DeviceInfo.getDeviceId()).toUpperCase(),
				EditField.DEFAULT_MAXCHARS, EditField.READONLY));

		if (tMan.state == TrialManager.STATE_REG) {
			add(new EditField("Registration Key: ", tMan.activationKey,
					EditField.DEFAULT_MAXCHARS, EditField.READONLY));
		}

		// Seperator
		add(new LabelField("", LabelField.NON_FOCUSABLE));

		StyledButtonField checkForUpdatesBtn = new StyledButtonField(
				"Check for Updates", StyledButtonField.SIZE_MID, 170, getFont());
		checkForUpdatesBtn.setFont(btnFont);
		checkForUpdatesBtn.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field field, int context) {
				Browser.getDefaultSession().displayPage(
						"http://www.blackberrysmart.com/updates.php?appName=bbphotopro&version="
								+ AppInfo.VERSION_STRING);
			}
		});

		StyledButtonField contactUsBtn = new StyledButtonField("Contact Us",
				StyledButtonField.SIZE_MID, 170, getFont());
		contactUsBtn.setChangeListener(new FieldChangeListener() {
			public void fieldChanged(Field field, int context) {
				MessageArguments mArgs = new MessageArguments(
						MessageArguments.ARG_NEW,
						"support@blackberrysmart.com",
						"BBSmart Photo Pro Support Query", "");

				Invoke.invokeApplication(Invoke.APP_TYPE_MESSAGES, mArgs);
			}
		});

		HorizontalFieldManager hfm2 = new HorizontalFieldManager(
				HorizontalFieldManager.FIELD_HCENTER);
		hfm2.add(checkForUpdatesBtn);
		add(hfm2);

		add(new SpacerField(SpacerField.MODE_VERT, 10, Color.WHITE));

		HorizontalFieldManager hfm3 = new HorizontalFieldManager(
				HorizontalFieldManager.FIELD_HCENTER);
		hfm3.add(contactUsBtn);
		add(hfm3);
	}

	public boolean onClose() {
		close();
		return true;
	}
}