package com.bbsmart.pda.blackberry.bbphoto.ui.customfields;

import com.bbsmart.pda.blackberry.bbphoto.BBSmartPhoto;
import com.bbsmart.pda.blackberry.bbphoto.util.Themes;
import com.bbsmart.pda.blackberry.bbphoto.util.UiUtil;

import net.rim.device.api.ui.Color;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FocusChangeListener;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.Screen;
import net.rim.device.api.ui.XYRect;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;

public final class BBPhotoDialog extends Screen {
	public static final int DELETE 	= 8;
	public static final int OK 		= 4;
	public static final int CANCEL 	= 1;
	
	private static int choiceIndex;
	private static boolean dontAskAgain;
	private Object[] choices;
	private LabelField message;
	private CheckboxField dontAskAgainField = new CheckboxField("Don't ask this again", false);
	
	private int maxWidth = 0;  // Used to layout the Option Labels
	
	// *************************** STATIC INVOKERS **************************
	public static void alert(String text) {
		Object[] choices = new Object[] {"Ok"};
		ask(text, choices);
	}
	
	public static int ask(String text, Object[] choices) {
		choiceIndex = -1;
		BBSmartPhoto.instance.pushModalScreen(new BBPhotoDialog(text, choices, 0, false));
		return choiceIndex;
	}
	
	public static int ask(String text, Object[] choices, boolean dontAskAgain) {
		choiceIndex = -1;
		BBSmartPhoto.instance.pushModalScreen(new BBPhotoDialog(text, choices, 0, dontAskAgain));
		return choiceIndex;
	}
	
	public static int askDelete(String text, int defaultValue, boolean dontAskAgain) {
		choiceIndex = -1;
		BBPhotoDialog.dontAskAgain = false;
		Object[] deleteChoices = new Object[] {"Delete", "Cancel"};
		int val;
		switch(defaultValue) {
			case DELETE:
				val = 0;
				break;
			case CANCEL:
				val = 1;
				break;
			default:
				val = 0;
		}
		BBSmartPhoto.instance.pushModalScreen(new BBPhotoDialog(text, deleteChoices, val, dontAskAgain));
		switch(choiceIndex) {
			case 0:
				return DELETE;
			case 1:
				return CANCEL;
			default:
				return -1;
		}
	}
	
	public static boolean getDontAskAgainValue() {
		return dontAskAgain;
	}
	
	// ************************** DIALOG SCREEN ***************************
	private VerticalFieldManager choiceManager = new VerticalFieldManager(
			Manager.VERTICAL_SCROLL | Manager.VERTICAL_SCROLLBAR | VerticalFieldManager.FIELD_HCENTER);
	
	private BBPhotoDialog(String title) {
		super(new VerticalFieldManager(), Screen.DEFAULT_CLOSE);
		message = new LabelField(title, LabelField.FIELD_HCENTER | LabelField.HCENTER) {
			protected void layout(int width, int height) {
				super.layout(width, height);
				setExtent(getWidth()+10, getHeight());
			}
		};
		setFont(getFont().derive(Font.BOLD));
		message.setPadding(5, 0, 0, 0);
		choiceManager.setPadding(0, 5, 5, 5);
		add(message);
		add(choiceManager);
	}
	
	private BBPhotoDialog(String title, Object[] choices, int defaultValue, boolean dontAskAgain) {
		this(title);
		this.choices = choices;
		addOptionRows();
		choiceManager.getField(defaultValue).setFocus();
		if(dontAskAgain) {
			choiceManager.add(dontAskAgainField);
		}
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
		OptionChoiceHFM mgr = new OptionChoiceHFM(OptionChoiceHFM.FIELD_HCENTER);
		OptionChoiceLabel choiceField = new OptionChoiceLabel(aChoice, 
				BBPhotoLabelField.FOCUSABLE | BBPhotoLabelField.HCENTER | BBPhotoLabelField.FIELD_HCENTER);
		choiceField.setFocusListener(mgr);

		HorizontalFieldManager iMgr = new HorizontalFieldManager();
		iMgr.setPadding(5, 5, 5, 5);
		iMgr.add(choiceField);
		mgr.add(iMgr);
		choiceManager.add(mgr);
	}

	protected boolean navigationMovement(int dx, int dy, int status, int time) {
		invalidate();
		return super.navigationMovement(dx, dy, status, time);
	}
	
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
		setPosition((Graphics.getScreenWidth()/2)-(this.getWidth()/2),
				(Graphics.getScreenHeight()/2)-(this.getHeight()/2));
	}
	
	// ************** CHOICE LABEL IN THE POPUP SCREEN ************
	protected class OptionChoiceLabel extends BBPhotoLabelField {

		public OptionChoiceLabel(String text, long style) {
			super(text, UiUtil.getOptionChoice(), style);
		}

		protected void layout(int aWidth, int aHeight) {
			super.layout(aWidth, aHeight);
			if(maxWidth+10 > getWidth()) {
				setExtent(maxWidth+10, getHeight());
			}
		}
		
		protected void paint(Graphics aGraphics) {
			aGraphics.setBackgroundColor(Graphics.FULL_WHITE);
			aGraphics.clear();
			super.paint(aGraphics);
		}

		protected boolean trackwheelClick(int status, int time) {
			choiceIndex = this.getManager().getManager().getIndex();
			dontAskAgain = dontAskAgainField.getChecked();
			getUiEngine().popScreen(getScreen());
			return true;
		}
	}
	
	// ****************** CONTAINER FOR CHOICE LABELS *********************
	private class OptionChoiceHFM extends HorizontalFieldManager implements FocusChangeListener {
		private Graphics graphics;
		private boolean inFocus;

		public OptionChoiceHFM(long style) {
			super(style);
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