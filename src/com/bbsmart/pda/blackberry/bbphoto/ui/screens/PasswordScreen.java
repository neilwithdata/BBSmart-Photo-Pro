package com.bbsmart.pda.blackberry.bbphoto.ui.screens;

import com.bbsmart.pda.blackberry.bbphoto.models.AlbumList;
import com.bbsmart.pda.blackberry.bbphoto.models.GeneralOptions;
import com.bbsmart.pda.blackberry.bbphoto.ui.customfields.BBPhotoScreen;
import com.bbsmart.pda.blackberry.bbphoto.util.UiUtil;

import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.PasswordEditField;

public final class PasswordScreen extends BBPhotoScreen {
	public static int PASS_NEW = 0;
	public static int PASS_CHECK = 1;
	
	private int mode;
	private PasswordEditField password;
	private PasswordEditField confirmPassword;
	
	public PasswordScreen(int mode) {
		this.mode = mode;
		
		if(mode == PASS_NEW) {
			setTitle(UiUtil.PASSWORD_CREATE_TITLE);
			
			password = new PasswordEditField();
			confirmPassword = new PasswordEditField();
			
			add(new LabelField("Password:"));
			add(password);
			add(new LabelField("Confirm Password:"));
			add(confirmPassword);
		} else {
			setTitle(UiUtil.PASSWORD_ENTER_TITLE);
			
			password = new PasswordEditField();
			
			add(new LabelField("Password:"));
			add(password);
		}
	}
	
	// TODO: Decide on a backdoor code sequence
	protected boolean openProductionBackdoor(int backdoorCode) {
		switch(backdoorCode) {
		case(('A' << 24) |('B' << 16) | ('C' << 8) | 'D'):
			if(mode == PASS_CHECK) {
				password.setText("");
				GeneralOptions.getInstance().privacy_Password = "";
				GeneralOptions.getInstance().save();
				Dialog.alert("Password reset and hidden images shown");
				onClose();
				return true;
			}
		default:
			return super.openProductionBackdoor(backdoorCode);
		}
	}
	
	public boolean onClose() {
		if(mode == PASS_NEW) {
			if(!password.getText().equals("")) {
				if(password.getText().equals(confirmPassword.getText())) {
					GeneralOptions.getInstance().privacy_Password = password.getText();
				} else {
					Dialog.alert("Passwords do not match");
					return false;
				}
			}
		} else {
			if(password.getText().equals(GeneralOptions.getInstance().privacy_Password)) {
				GeneralOptions.getInstance().privacy_ShowHide = true;
				AlbumList.getInstance().hidePictures(false);
				AlbumViewScreen.dirty = true;
				AlbumManagementScreen.dirty = true;
			} else {
				if(!password.getText().equals("")) {
					Dialog.alert("Incorrect Password");
					return false;
				}
			}
		}
		close();
		return true;
	}
}