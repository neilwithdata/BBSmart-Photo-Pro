package com.bbsmart.pda.blackberry.bbphoto.util;

import com.bbsmart.pda.blackberry.bbphoto.models.GeneralOptions;

import net.rim.device.api.ui.Color;

public final class Themes {
	private static Object[] themes = new Object[] {
				new Theme("Blue Floral", Color.BLUE, Color.BLUE, Color.BLACK ,Color.RED, Color.BLUE, Color.YELLOW, Color.LIGHTGREEN),
				new Theme("Black Grunge", Color.BLACK, Color.RED, Color.BLACK, Color.RED, Color.BLACK, Color.YELLOW, Color.LIGHTGREEN),
				new Theme("Olive Tech", Color.OLIVE, Color.OLIVE, Color.BLACK, Color.RED, Color.OLIVE, Color.YELLOW, Color.LIGHTGREEN),
				new Theme("BBSmart", Color.RED, Color.RED, Color.BLACK, Color.RED, Color.BLACK, Color.YELLOW, Color.LIGHTGREEN)
			};

	public static Object[] getThemes() {
		return themes;
	}
	
	private static Theme currentTheme;
	
	public static class Theme {
		private String themeName;
		
		private int mainColor;

		// Text colours
		private int textFocusColor;
		private int textUnfocusColor;
		
		// Border colours
		private int borderFocusColor;
		private int borderUnfocusColor;
		private int borderMoveColor;
		private int borderSelectColor;
		
		public Theme(String name, int mainColor, int textFocusColor, int textUnfocusColor, 
													int borderFocusColor, int borderUnfocusColor, int borderMoveColor, int borderSelectColor) {
			themeName = name;
			
			this.mainColor = mainColor;
			
			this.textFocusColor = textFocusColor;
			this.textUnfocusColor = textUnfocusColor;
			
			this.borderFocusColor = borderFocusColor;
			this.borderUnfocusColor = borderUnfocusColor;
			this.borderMoveColor = borderMoveColor;
			this.borderSelectColor = borderSelectColor;
		}
		
		public String getTheme() {
			return themeName;
		}
		
		public int getThemeColor() {
			return mainColor;
		}
		
		public int getBorderFocusColor() {
			return borderFocusColor;
		}
		
		public int getBorderUnfocusColor() {
			return borderUnfocusColor;
		}
		
		public int getBorderMoveColor() {
			return borderMoveColor;
		}
		
		public int getBorderSelectColor() {
			return borderSelectColor;
		}

		public int getTextFocusColor() {
			return textFocusColor;
		}
		
		public int getTextUnfocusColor() {
			return textUnfocusColor;
		}
		
		public String toString() {
			return themeName;
		}
	}
	
	public static void setCurrentTheme(String theme) {
		for(int i = 0; i < themes.length; i++) {
			if(themes[i].toString().equals(theme)) {
				currentTheme = (Theme)themes[i];
				return;
			}
		}
	}
	
	public static Theme getCurrentTheme() {
		if(currentTheme == null) {
			setCurrentTheme(GeneralOptions.getInstance().app_Theme);
		}
		return currentTheme;
	}
}