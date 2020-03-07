package com.bbsmart.pda.blackberry.bbphoto.ui.customfields;

import net.rim.device.api.ui.component.ObjectChoiceField;

// Object Choice Field that does not truncate the values in its choice field with ...
// A maximum width can also be set
public class BBPhotoObjectChoiceField extends ObjectChoiceField {
	private int maxWidth = -1;
	
	public BBPhotoObjectChoiceField() {
		super();
	}
	
    public BBPhotoObjectChoiceField(String label, Object[] choices) {
    	super(label, choices);
    }

    public BBPhotoObjectChoiceField(String label, Object[] choices, int initialIndex) { 
    	super(label, choices, initialIndex);
    }
	
	public BBPhotoObjectChoiceField(String label, Object[] choices,	int initialIndex, long style) {
		super(label, choices, initialIndex, style);
	}

	public BBPhotoObjectChoiceField(String label, Object[] choices, Object initialObject) { 
		super(label, choices, initialObject);
	}
	
	public BBPhotoObjectChoiceField(String label, Object[] choices, Object initialObject, int maxWidth) {
		this(label, choices, initialObject);
		this.maxWidth = maxWidth;
	}

	protected int getWidthOfChoice(int index) {
		int width = 0;
		for(int i = 0; i < this.getSize(); i++) {
			int temp = this.getFont().getAdvance(this.getChoice(i).toString());
			if(temp > width) {
				width = temp;
			}
		}
		if(maxWidth > 0) {
			return width > maxWidth ? maxWidth : width;
		}
		return width;
	}
}