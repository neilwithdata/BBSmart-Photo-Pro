package com.bbsmart.pda.blackberry.bbphoto.util;

public final class ViewChoices {
	private static Object[] viewChoices = null;
	
	public static Object[] getViewChoices() {
		if(viewChoices == null) {
			viewChoices = new Object[] {
				new ViewChoice(0, "Thumbnails"),
				new ViewChoice(1, "Details"),
				new ViewChoice(2, "List") };
		}
		return viewChoices;
	}
	
	public static class ViewChoice {
		private int viewIndex = -1;
		
		private String viewName = null;
		
		public ViewChoice(int index, String name) {
			viewIndex = index;
			viewName = name;
		}

		public int getIndex() {
			return viewIndex;
		}
		
		public String getView() {
			return viewName;
		}
		
		public String toString() {
			return viewName;
		}
	}
	
	public static int getIndexOfView(String view) {
		Object[] viewChoices = getViewChoices();
		int numChoices = viewChoices.length;
		for (int i = 0; i < numChoices; i++) {
			ViewChoice choice = (ViewChoice) viewChoices[i];
			if (choice.getView().equals(view)) {
				return choice.getIndex();
			}
		}
		return -1;
	}
	
	public static String getViewOfIndex(int index) {
		Object[] viewChoices = getViewChoices();
		int numChoices = viewChoices.length;
		for (int i = 0; i < numChoices; i++) {
			ViewChoice choice = (ViewChoice) viewChoices[i];
			if (choice.getIndex() == index) {
				return choice.getView();
			}
		}
		return null;
	}
}