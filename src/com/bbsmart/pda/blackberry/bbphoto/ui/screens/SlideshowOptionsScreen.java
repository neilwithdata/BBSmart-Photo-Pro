package com.bbsmart.pda.blackberry.bbphoto.ui.screens;

import com.bbsmart.pda.blackberry.bbphoto.models.SlideshowOptions;
import com.bbsmart.pda.blackberry.bbphoto.ui.customfields.BBPhotoObjectChoiceField;
import com.bbsmart.pda.blackberry.bbphoto.ui.customfields.BBPhotoOptionsScreen;
import com.bbsmart.pda.blackberry.bbphoto.util.UiUtil;

import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.NumericChoiceField;

public final class SlideshowOptionsScreen extends BBPhotoOptionsScreen {
	private NumericChoiceField slideDelay;
	private BBPhotoObjectChoiceField transEffect;
	private BBPhotoObjectChoiceField randSeq;
	private CheckboxField showNotes;
	
	public SlideshowOptionsScreen() {
		setTitle(UiUtil.OPTIONS_SLIDESHOW_TITLE);
		
		slideDelay = new NumericChoiceField("Slide Delay", 3, 10, 1);
		slideDelay.setSelectedValue(SlideshowOptions.getInstance().slideDelay);
		transEffect = new BBPhotoObjectChoiceField("Transition Effect", 
				new Object[] {"None", "Random", "Up", "Down", "Horizontal", "Vertical", "Fade"}, 
					SlideshowOptions.getInstance().transEffect);
		randSeq = new BBPhotoObjectChoiceField("Play Order", 
				new Object[] {"Random", "Sequential"}, SlideshowOptions.getInstance().randSeq);
		showNotes = new CheckboxField("Show Notes", SlideshowOptions.getInstance().showNotes);
		
		add(slideDelay);
		add(transEffect);
		add(randSeq);
		add(showNotes);
	}
	
	public boolean onClose() {
		SlideshowOptions.getInstance().slideDelay = slideDelay.getSelectedValue();
		SlideshowOptions.getInstance().transEffect = transEffect.getSelectedIndex();
		SlideshowOptions.getInstance().randSeq = randSeq.getSelectedIndex();
		SlideshowOptions.getInstance().showNotes = showNotes.getChecked();
		
		SlideshowOptions.getInstance().save();
		close();
		return true;
	}
}