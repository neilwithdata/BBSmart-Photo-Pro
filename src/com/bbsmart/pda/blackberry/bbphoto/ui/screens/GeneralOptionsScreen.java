package com.bbsmart.pda.blackberry.bbphoto.ui.screens;

import com.bbsmart.pda.blackberry.bbphoto.BBSmartPhoto;
import com.bbsmart.pda.blackberry.bbphoto.models.AlbumList;
import com.bbsmart.pda.blackberry.bbphoto.models.GeneralOptions;
import com.bbsmart.pda.blackberry.bbphoto.ui.customfields.BBPhotoObjectChoiceField;
import com.bbsmart.pda.blackberry.bbphoto.ui.customfields.BBPhotoOptionsScreen;
import com.bbsmart.pda.blackberry.bbphoto.ui.customfields.DetailField;
import com.bbsmart.pda.blackberry.bbphoto.ui.customfields.ListView;
import com.bbsmart.pda.blackberry.bbphoto.util.ImageFileUtil;
import com.bbsmart.pda.blackberry.bbphoto.util.Themes;
import com.bbsmart.pda.blackberry.bbphoto.util.UiUtil;

import net.rim.device.api.ui.component.CheckboxField;

public final class GeneralOptionsScreen extends BBPhotoOptionsScreen {
	private CheckboxField isPrivate;
	private CheckboxField promptImgDelete;
	private BBPhotoObjectChoiceField theme;
	
	public GeneralOptionsScreen() {
		setTitle(UiUtil.OPTIONS_GENERAL_TITLE);
		
		isPrivate = new CheckboxField("Show All Private", GeneralOptions.getInstance().privacy_ShowHide);
		promptImgDelete = new CheckboxField("Ask on Delete", GeneralOptions.getInstance().app_PromptIMGDelete);
		theme = new BBPhotoObjectChoiceField("Theme", Themes.getThemes(), Themes.getCurrentTheme());
		
		add(isPrivate);
		add(promptImgDelete);
		add(theme);
	}
	
	public boolean onClose() {
		if(isPrivate.getChecked() != GeneralOptions.getInstance().privacy_ShowHide) {
			if(!isPrivate.getChecked() && GeneralOptions.getInstance().privacy_Password.equals("")) {
				// Chosen to hide private images and no password is set (First change of option)
				BBSmartPhoto.instance.pushModalScreen(new PasswordScreen(PasswordScreen.PASS_NEW));
				if(GeneralOptions.getInstance().privacy_Password.equals("")) {
					// Password was not set so do not allow the change
					isPrivate.setChecked(true);
				} else {
					AlbumList.getInstance().hidePictures(true);
				}
				GeneralOptions.getInstance().privacy_ShowHide = isPrivate.getChecked();
				AlbumViewScreen.dirty = true;
				AlbumManagementScreen.dirty = true;
			} else if(!isPrivate.getChecked()) {
				// Chosen to hide private with a password set so allow it
				GeneralOptions.getInstance().privacy_ShowHide = isPrivate.getChecked();
				AlbumList.getInstance().hidePictures(true);
				AlbumViewScreen.dirty = true;
				AlbumManagementScreen.dirty = true;
			} else if(isPrivate.getChecked()) {
				// Chosen to show private images and a password is set
				// If password was incorrect do not allow the change
				BBSmartPhoto.instance.pushScreen(new PasswordScreen(PasswordScreen.PASS_CHECK));
			}	
		}
		GeneralOptions.getInstance().app_PromptIMGDelete = promptImgDelete.getChecked();
		if(!GeneralOptions.getInstance().app_Theme.equals(theme.getChoice(theme.getSelectedIndex()).toString())) {
			GeneralOptions.getInstance().app_Theme = theme.getChoice(theme.getSelectedIndex()).toString();
			
			Themes.setCurrentTheme(GeneralOptions.getInstance().app_Theme);
			AlbumViewScreen.themeValid = false;
			DetailField.background = ImageFileUtil.getThemeImage(UiUtil.getDetailField());
			ListView.background = ImageFileUtil.getThemeImage(UiUtil.getListField());
			BBSmartPhoto.instance.relayout();
		}
		GeneralOptions.getInstance().save();
		close();
		return true;
	}
}