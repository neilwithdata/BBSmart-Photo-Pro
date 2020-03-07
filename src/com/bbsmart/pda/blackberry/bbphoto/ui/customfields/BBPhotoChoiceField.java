package com.bbsmart.pda.blackberry.bbphoto.ui.customfields;

import java.util.Vector;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.KeypadListener;
import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.Ui;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;

import com.bbsmart.pda.blackberry.bbphoto.ui.customfields.BBPhotoChoiceField.OptionPickerScreen.OptionChoiceLabel;
import com.bbsmart.pda.blackberry.bbphoto.util.Themes;
import com.bbsmart.pda.blackberry.bbphoto.util.UiUtil;

public final class BBPhotoChoiceField extends HorizontalFieldManager {
	private OptionPickerInnerField innerOPF;
	private HorizontalFieldManager innerHFM;
	private FieldChangeListener changeListener;
	private BBPhotoChoiceField thisField;
	private Vector choiceLabels = new Vector();

	private String choicesValue;
	private Object[] choices;

	private String labelBackground;
	
	/**
	 * Allows a change listener to identify the field firing off a change.
	 * Useful if you have the same change listener for multiple fields of this
	 * type.
	 */
	private int context;

	/**
	 * BBPhotoChoiceField allows the choosing of an option from a popup menu.
	 * 
	 * @param aChoicesValue
	 *            The initial value to display in the choice label.
	 * @param aChangeListener
	 *            The listener who cares about changes.
	 * @param aContext
	 *            The context for the field change event context argument.
	 */
	public BBPhotoChoiceField(String aChoicesValue, Object[] aChoices, 
			FieldChangeListener aChangeListener, int aContext, String aLabelBackground) {
		super(HorizontalFieldManager.FIELD_HCENTER);
		context = aContext;
		thisField = this;
		
		changeListener = aChangeListener;
		choicesValue = aChoicesValue;
		choices = aChoices;
		labelBackground = aLabelBackground;
		
		innerHFM = new HorizontalFieldManager();
		innerOPF = new OptionPickerInnerField();
		innerHFM.add(innerOPF);
		add(innerHFM);
	}

	private OptionChoiceLabel updateFocus() {
		int vecSize = choiceLabels.size();
		for (int i = 0; i < vecSize; i++) {
			OptionChoiceLabel label = (OptionChoiceLabel) choiceLabels
					.elementAt(i);
			if (label.getText().equals(choicesValue)) {
				label.setFocus();
				return label;
			}
		}
		return null;
	}

	public void updateTheme() {
		innerOPF.updateTheme();
		for(int i = 0; i < choiceLabels.size(); i++) {
			((OptionChoiceLabel)choiceLabels.elementAt(i)).updateTheme();
		}
	}
	
	protected void setChoice(int index) {
		if(index < 0) { index = choices.length-1; } else
		if(index >= choices.length) { index = 0; }
		setSelectedIndex(index);
	}
	
	protected void setChoice(String choice) {
		choicesValue = choice;
		innerOPF.setText(choicesValue);
	}
	
	// ******************* OBJECT CHOICE FIELD METHODS **************
	public int getSelectedIndex() {
		for(int i = 0; i < choices.length; i++) {
			if(choicesValue.equals(choices[i].toString())) {
				return i;
			}
		}
		return -1;
	}

	public void setSelectedIndex(int index) {
		setChoice(choices[index].toString());
	}
	
	public void setSelectedIndex(String choice) {
		int index;
		for(index = choices.length-1; index >= 0; index--) {
			if(choice.equals(choices[index].toString())) {
				break;
			}
		}
		setChoice(index);
	}

	public String getChoice() {
		return choicesValue;
	}

	public void setChoices(Object[] choices) {
		this.choices = choices;
		innerOPF.optionPickerScreen = null;
		choiceLabels.removeAllElements();
	}

	public void displayOptions() {
		innerOPF.displayOptions();
	}
	// ***************** END OBJECT CHOICE FIELD METHODS *******************
	
	// ********************* CHOICE FIELD LABEL OF CURRENT CHOICE ********************
	private class OptionPickerInnerField extends BBPhotoLabelField implements FocusChangeListener {
		private OptionPickerScreen optionPickerScreen;
		private Bitmap overlay = UiUtil.getGlassyOverlay();
		
		private OptionPickerInnerField() {
			super("", labelBackground, BBPhotoLabelField.FOCUSABLE | BBPhotoLabelField.HCENTER);
			setFocusListener(this);
			setColors(Graphics.FULL_BLACK, Graphics.FULL_WHITE);
			setText(choicesValue);
		}
		
		public void updateTheme() {
			overlay = UiUtil.getGlassyOverlay();
		}
		
		protected void paint(Graphics g) {
			super.paint(g);
			if(!isFocus()) { g.setGlobalAlpha(125); }
			g.drawBitmap(0, 0, getWidth(), getHeight(), overlay, 0, 0);
			this.writeText(g, Graphics.FULL_BLACK);
		}
		
		protected void displayOptions() {
			if (optionPickerScreen == null) {
				optionPickerScreen = new OptionPickerScreen();
			}

			Ui.getUiEngine().pushScreen(optionPickerScreen);

			OptionChoiceLabel focused = updateFocus();
			if (focused != null) {
				int size = choiceLabels.size();
				int index = 0;
				for (int i = 0; i < size; i++) {
					OptionChoiceLabel label = (OptionChoiceLabel) choiceLabels
							.elementAt(i);
					if (label.getText() == focused.getText()) {
						index = i;
						break;
					}
				}
				if (index < (size - 3) && (index > 3)) {
					OptionChoiceLabel label = (OptionChoiceLabel) choiceLabels
							.elementAt(index + 2);
					label.setFocus();
					label = (OptionChoiceLabel) choiceLabels
							.elementAt(index - 2);
					label.setFocus();
					focused.setFocus();
				}
			}
		}

		// ***********************  TRACKWHEEL HANDLER *******************
		protected boolean trackwheelClick(int status, int time) {
			displayOptions();
			return true;
		}

		protected int moveFocus(int amount, int status, int time) {
			if((status & KeypadListener.STATUS_ALT) == KeypadListener.STATUS_ALT) {
				int idx = thisField.getSelectedIndex()+amount;
				thisField.setChoice(idx);
				changeListener.fieldChanged(thisField, FieldChangeListener.PROGRAMMATIC);
				return 0;
			}
			return super.moveFocus(amount, status, time);
		}
		
		// ************************* KEYSTROKE HANDLER **********************
		protected boolean keyChar(char c, int status, int time) {
			switch(c) {
				case Keypad.KEY_SPACE:
					int index = thisField.getSelectedIndex();
					if((status & KeypadListener.STATUS_SHIFT) == KeypadListener.STATUS_SHIFT) {
						thisField.setChoice(index-1);
					} else {
						thisField.setChoice(index+1);
					}
					changeListener.fieldChanged(thisField, FieldChangeListener.PROGRAMMATIC);
					break;
				default:
					return super.keyChar(c, status, time);
			}
			return true;
		}
		
		public void focusChanged(Field aField, int aEventType) {
			switch (aEventType) {
			case FOCUS_LOST:
			case FOCUS_GAINED:
				invalidate();
				break;
			default:
				break;
			}
		}
	}

	// ******************** POPUP SCREEN OF CHOICES *******************
	protected class OptionPickerScreen extends Screen {
		private VerticalFieldManager choiceManager = new VerticalFieldManager(
				VerticalFieldManager.VERTICAL_SCROLL | VerticalFieldManager.VERTICAL_SCROLLBAR | VerticalFieldManager.FIELD_HCENTER);
		private int maxWidth;
		
		public OptionPickerScreen() {
			super(new VerticalFieldManager(), Screen.DEFAULT_CLOSE);
			setFont(getFont().derive(Font.BOLD));
			addOptionRows();
			choiceManager.setPadding(5, 5, 5, 5);
			add(choiceManager);
		}

		private void addOptionRows() {
			for (int i = 0; i < choices.length; i++) {
				String choice = choices[i].toString();
				int width = getFont().derive(Font.BOLD).getAdvance(choice);
				if(width > maxWidth) { maxWidth = width; }
				addChoiceRow(choice);
			}
		}

		private void addChoiceRow(String aChoice) {
			OptionChoiceHFM mgr = new OptionChoiceHFM();
			OptionChoiceLabel choiceField = new OptionChoiceLabel(aChoice, 
					BBPhotoLabelField.FOCUSABLE | BBPhotoLabelField.HCENTER | BBPhotoLabelField.FIELD_HCENTER);
			choiceField.setFocusListener(mgr);
			choiceLabels.addElement(choiceField);

			HorizontalFieldManager iMgr = new HorizontalFieldManager();
			iMgr.setPadding(5, 5, 5, 5);
			iMgr.add(choiceField);
			mgr.add(iMgr);
			choiceManager.add(mgr);
		}

		public boolean keyChar(char key, int status, int time) {
			switch (key) {
			case Characters.ESCAPE:
				onClose();
				return true;
			}
			return super.keyChar(key, status, time);
		}

		// ******************* LAYOUT AND PAINT ***********************
		protected void paint(Graphics graphics) {
			int color = graphics.getColor();
			XYRect clip = graphics.getClippingRect();
			
			graphics.setColor(Graphics.FULL_WHITE);
			graphics.clear();
			
			graphics.setColor(Themes.getCurrentTheme().getThemeColor());
			graphics.drawRect(clip.x, clip.y, clip.width, clip.height);
			graphics.drawRect(clip.x+1, clip.y+1, clip.width-2, clip.height-2);
			
			graphics.setColor(color);
			super.paint(graphics);
		}
		
		protected void sublayout(int aWidth, int aHeight) {
			layoutDelegate(aWidth, aHeight);
			int maxWidth = 0;
			int height = 0;
			for(int i = 0; i < getFieldCount(); i++) {
				Field f = getField(i);
				height = height + f.getHeight();
				if(f.getWidth() > maxWidth) {
					maxWidth = f.getWidth();
				}
			}
			setExtent(maxWidth, height);
			setPosition(Graphics.getScreenWidth()-this.getWidth(),0);
		}
		
		// ************** CHOICE LABEL IN THE POPUP SCREEN ************
		protected class OptionChoiceLabel extends BBPhotoLabelField {

			public OptionChoiceLabel(String text, long style) {
				super(text, UiUtil.getOptionChoice(), style);
			}

			protected void paint(Graphics aGraphics) {
				aGraphics.setBackgroundColor(Graphics.FULL_WHITE);
				aGraphics.clear();
				super.paint(aGraphics);
			}

			protected void layout(int aWidth, int aHeight) {
				super.layout(aWidth, aHeight);
				if(maxWidth+10 > getWidth()) {
					setExtent(maxWidth+10, getHeight());
				}
			}

			protected boolean trackwheelClick(int status, int time) {
				choicesValue = this.getText();
				innerOPF.setText(choicesValue);
				innerHFM.invalidate();
				changeListener.fieldChanged(thisField, context);
				getUiEngine().popScreen(getScreen());
				return true;
			}
		}
		
		// ****************** CONTAINER FOR CHOICE LABELS *********************
		private class OptionChoiceHFM extends HorizontalFieldManager implements FocusChangeListener {
			private Graphics graphics;
			private boolean inFocus;

			public OptionChoiceHFM() {
			}

			protected void subpaint(Graphics aGraphics) {
				graphics = aGraphics;
				aGraphics.clear();
				int bwidth = getExtent().width;
				int bheight = getExtent().height;
				if (inFocus) {
					graphics.setColor(Themes.getCurrentTheme().getTextFocusColor());
					graphics.drawRect(0, 0, bwidth, bheight);
					graphics.drawRect(1, 1, bwidth - 2, bheight - 2);
				} else {
					graphics.setColor(Color.WHITE);
					graphics.drawRect(0, 0, bwidth, bheight);
					graphics.drawRect(1, 1, bwidth - 2, bheight - 2);
					graphics.drawRect(2, 2, bwidth - 4, bheight - 4);
					graphics.drawRect(3, 3, bwidth - 6, bheight - 6);
					graphics.drawRect(4, 4, bwidth - 8, bheight - 8);
					graphics.drawRect(5, 5, bwidth - 10, bheight - 10);
				}
				super.subpaint(aGraphics);
			}

			public void focusChanged(Field aField, int aEventType) {
				switch (aEventType) {
				case FOCUS_GAINED:
					inFocus = true;
					break;
				case FOCUS_LOST:
					inFocus = false;
					break;
				default:
					break;
				}
				invalidate();
			}
		}
	}
}