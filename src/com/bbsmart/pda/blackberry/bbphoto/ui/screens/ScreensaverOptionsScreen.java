package com.bbsmart.pda.blackberry.bbphoto.ui.screens;

import com.bbsmart.pda.blackberry.bbphoto.models.AlbumList;
import com.bbsmart.pda.blackberry.bbphoto.models.ScreensaverOptions;
import com.bbsmart.pda.blackberry.bbphoto.ui.customfields.BBPhotoObjectChoiceField;
import com.bbsmart.pda.blackberry.bbphoto.ui.customfields.BBPhotoOptionsScreen;
import com.bbsmart.pda.blackberry.bbphoto.util.UiUtil;

import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.NumericChoiceField;

public final class ScreensaverOptionsScreen extends BBPhotoOptionsScreen {
	private CheckboxField enabled;
	private NumericChoiceField slideDelay;
	private BBPhotoObjectChoiceField albumList;
	private BBPhotoObjectChoiceField randSeq;
	private BBPhotoObjectChoiceField transEffect;
	private BBPhotoObjectChoiceField startDelay;
	private NumericChoiceField timeOut;
	private NumericChoiceField batteryStop;
	private CheckboxField backlight;
	
	public ScreensaverOptionsScreen() {
		setTitle(UiUtil.OPTIONS_SCNSAVER_TITLE);
		
		enabled = new CheckboxField("Enable", ScreensaverOptions.getInstance().enabled);
		
		slideDelay = new NumericChoiceField("Slide Delay (sec)", 3, 10, 1);
		slideDelay.setSelectedValue(ScreensaverOptions.getInstance().slideDelay);
		
		albumList = new BBPhotoObjectChoiceField("Play using Album", 
				AlbumList.getInstance().listAlbums(), ScreensaverOptions.getInstance().useAlbum, 
				UiUtil.DEVICE_240W ? 80 : 160);
		
		randSeq = new BBPhotoObjectChoiceField("Play Order", 
				new Object[] {"Random", "Sequential"}, ScreensaverOptions.getInstance().randSeq);
		
		transEffect = new BBPhotoObjectChoiceField("Transition Effect", 
				new Object[] {"None", "Random", "Up", "Down", "Horizontal", "Vertical", "Fade"}, 
								ScreensaverOptions.getInstance().transEffect);
		
		startDelay = new BBPhotoObjectChoiceField("Start After (min)", 
				new Object[] {"1", "5", "10", "20", "30"}, ""+ScreensaverOptions.getInstance().startDelay);
		
		timeOut = new NumericChoiceField("Stop After (hours)", 1, 5, 1);
		timeOut.setSelectedValue(ScreensaverOptions.getInstance().timeOut);
		
		batteryStop = new NumericChoiceField("Stop if Battery (%)", 15, 50, 5);
		batteryStop.setSelectedValue(ScreensaverOptions.getInstance().batteryStop);
		
		backlight = new CheckboxField("Keep Backlight On", ScreensaverOptions.getInstance().backlight);
		
		add(enabled);
		add(slideDelay);
		add(albumList);
		add(randSeq);
		add(transEffect);
		add(startDelay);
		add(timeOut);
		add(batteryStop);
		add(backlight);
	}
	
	public boolean onClose() {
		ScreensaverOptions.getInstance().enabled = enabled.getChecked();
		ScreensaverOptions.getInstance().slideDelay = slideDelay.getSelectedValue();
		ScreensaverOptions.getInstance().useAlbum = (String)albumList.getChoice(albumList.getSelectedIndex());
		ScreensaverOptions.getInstance().randSeq = randSeq.getSelectedIndex();
		ScreensaverOptions.getInstance().transEffect = transEffect.getSelectedIndex();
		ScreensaverOptions.getInstance().startDelay = Integer.parseInt((String)startDelay.getChoice(startDelay.getSelectedIndex()));
		ScreensaverOptions.getInstance().timeOut = timeOut.getSelectedValue();
		ScreensaverOptions.getInstance().batteryStop = batteryStop.getSelectedValue();
		ScreensaverOptions.getInstance().backlight = backlight.getChecked();
		
		ScreensaverOptions.getInstance().save();
		close();
		return true;
	}
}