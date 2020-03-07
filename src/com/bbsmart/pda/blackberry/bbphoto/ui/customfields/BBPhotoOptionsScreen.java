package com.bbsmart.pda.blackberry.bbphoto.ui.customfields;

import java.util.Vector;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.ChoiceField;
import net.rim.device.api.ui.container.HorizontalFieldManager;

import com.bbsmart.pda.blackberry.bbphoto.util.UiUtil;

public class BBPhotoOptionsScreen extends BBPhotoScreen {
	private static final Bitmap FOCUS_BACKGND = UiUtil.getBlackHeading();
	
	private Vector spacers = new Vector();
	private int divider = Graphics.getScreenWidth();
	
	public void add(Field f) {
		String label = "Option";
		if(f instanceof ChoiceField) {
			int i = Graphics.getScreenWidth()-((ChoiceField)f).getWidthOfChoices();
			if(divider > i-10) {
				divider = i-10;
			}
			label = ((ChoiceField)f).getLabel();
			((ChoiceField)f).setLabel("");
		} else if(f instanceof CheckboxField) {
			label = ((CheckboxField)f).getLabel();
			((CheckboxField)f).setLabel("");
		}
		super.add(new Option(label, f));
	}

	protected void sublayout(int width, int height) {
		for(int i = 0; i < spacers.size(); i++) {
			((SpacerField)spacers.elementAt(i)).updateSpace(SpacerField.MODE_HORIZ, Graphics.getScreenWidth()-divider-15);
		}
		super.sublayout(width, height);
	}
	
	protected void paint(Graphics graphics) {
		super.paint(graphics);
		graphics.drawLine(divider, 21, divider, Graphics.getScreenHeight());
		graphics.drawLine(divider+1, 21, divider+1, Graphics.getScreenHeight());
	}
	
	private class Option extends HorizontalFieldManager {
		public Option(String label, Field options) {
			super();
			add(new OptionLabel(label, options));
			if(options instanceof CheckboxField) {
				SpacerField spacer = new SpacerField();
				spacers.addElement(spacer);
				add(spacer);
			}
			add(options);
		}
		
		protected void onFocus(int direction) {
			invalidate();
			super.onFocus(direction);
		}
		
		protected void onUnfocus() {
			invalidate();
			super.onUnfocus();
		}
	
		private class OptionLabel extends Field {
			
			private String label;
			private Field options;
			
			public OptionLabel(String text, Field f) {
				label = text;
				options = f;
			}
			
			protected void layout(int width, int height) {
				setExtent(divider-5, getFont().getHeight());
			}
			
			protected void paint(Graphics g) {
				if(options.isFocus()) {
					g.drawBitmap(0, 0, getWidth(), getHeight(), FOCUS_BACKGND, 0, 0);
					g.setColor(Graphics.FULL_WHITE);
				} else {
					g.setColor(Graphics.FULL_BLACK);
				}
				g.drawText(label, 0, 0);
			}
		}
	}
}