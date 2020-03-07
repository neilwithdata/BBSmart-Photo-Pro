package com.bbsmart.pda.blackberry.bbphoto.ui.screens;

import com.bbsmart.pda.blackberry.bbphoto.BBSmartPhoto;
import com.bbsmart.pda.blackberry.bbphoto.ui.customfields.BBPhotoScreen;
import com.bbsmart.pda.blackberry.bbphoto.ui.customfields.OptionsChoiceField;
import com.bbsmart.pda.blackberry.bbphoto.util.UiUtil;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;

public final class OptionsScreen extends BBPhotoScreen {
	private OptionsChoiceField choiceField;
	
    public OptionsScreen() { 
    	setTitle(UiUtil.OPTIONS_TITLE);
    	choiceField = new OptionsChoiceField();
    	add(choiceField);
    	
    	choiceField.setChangeListener(OptionsChoiceField.ITEM_SLIDESHOW, new FieldChangeListener() {
			public void fieldChanged(Field field, int context) {
				BBSmartPhoto.instance.pushScreen(new SlideshowOptionsScreen());
			}
		});
    	
    	choiceField.setChangeListener(OptionsChoiceField.ITEM_SCREENSAVER, new FieldChangeListener() {
			public void fieldChanged(Field field, int context) {
				BBSmartPhoto.instance.pushScreen(new ScreensaverOptionsScreen());
			}
		});
    	
    	choiceField.setChangeListener(OptionsChoiceField.ITEM_GENERAL, new FieldChangeListener() {
			public void fieldChanged(Field field, int context) {
				BBSmartPhoto.instance.pushScreen(new GeneralOptionsScreen());
			}
		});
    }
} 
