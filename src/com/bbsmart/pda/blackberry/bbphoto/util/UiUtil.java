package com.bbsmart.pda.blackberry.bbphoto.util;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.DeviceInfo;

public final class UiUtil {
	public static final String DEVICE_NAME = DeviceInfo.getDeviceName();

	public static final boolean DEVICE_240W = (DEVICE_NAME.startsWith("7") || DEVICE_NAME
			.startsWith("81"));

	public static final boolean DEVICE_SURETYPE = (DEVICE_NAME.startsWith("71") || DEVICE_NAME
			.startsWith("81"));

	// 81xx's, 83xx's and 88xx's all have trackballs rather than trackwheels
	public static final boolean HAS_TRACKBALL = (DEVICE_NAME.startsWith("81")
			|| DEVICE_NAME.startsWith("83") || DEVICE_NAME.startsWith("88"));
	
	public static final String BLACK_BAR = "black_bar.png";
	
	public static final String PRIVATE_ICON = "private.png";
	
	// ****************** Screen Titles
	public static final String IMG_PROP_TITLE = "title_imgProp.png";
	
	public static final String ALBUMS_TITLE = "title_albums.png";
	
	public static final String NEW_ALBUM_TITLE = "title_albumNew.png";
	
	public static final String ALBUM_PROP_TITLE = "title_albumProp.png";
	
	public static final String OPTIONS_TITLE = "title_options.png";
	
	public static final String OPTIONS_GENERAL_TITLE = "title_generalOpt.png";
	
	public static final String OPTIONS_SCNSAVER_TITLE = "title_scnsaverOpt.png";
	
	public static final String OPTIONS_SLIDESHOW_TITLE = "title_slideshowOpt.png";
	
	public static final String PASSWORD_CREATE_TITLE = "title_passNew.png";
	
	public static final String PASSWORD_ENTER_TITLE = "title_passEnter.png";
	
	public static final String TRIAL_ENDED = "title_trialEnded.png";
	
	public static final String TRIAL_REGISTER = "title_register.png";

	// ******************* UI Skin Files
	public static final String TITLE_320 = "title_320.png";
	
	public static final String TITLE_240 = "title_240.png";
	
	public static final String GLASSY_OVERLAY = "glassy_overlay.png";
		
	public static final String BACKGROUND_320 = "background_320.png";
	
	public static final String BACKGROUND_240 = "background_240.png";
	
	public static final String VIEW_SELECT_320 = "viewSelect_320x17.png";
	
	public static final String VIEW_SELECT_240 = "viewSelect_240x17.png";
	
	public static final String ALBUM_SELECT_320 = "albumSelect_320x17.png";
	
	public static final String ALBUM_SELECT_240 = "albumSelect_240x17.png";
	
	public static final String IMAGE_TRACKER_320 = "imageTracker_320x17.png";
	
	public static final String IMAGE_TRACKER_240 = "imageTracker_240x17.png";
	
	public static final String STATUS_BAR_320 = "statusBar_320x17.png";
	
	public static final String STATUS_BAR_240 = "statusBar_240x17.png";
	
	public static final String OPTION_CHOICE = "optionChoice.png";
	
	public static final String DETAIL_FIELD_320 = "detailField_320.png";
		
	public static final String DETAIL_FIELD_240 = "detailField_240.png";
	
	public static final String LIST_FIELD_320 = "listField_320.png";
	
	public static final String LIST_FIELD_240 = "listField_240.png";
	
	public static Bitmap getBlackHeading() {
		return ImageFileUtil.getResourceImage(BLACK_BAR);
	}
	
	public static Bitmap getPrivateIcon() {
		return ImageFileUtil.getResourceImage(PRIVATE_ICON);
	}
	
	public static final Bitmap LOADING = get("loading.png");
	
	public static final Bitmap SEPARATOR = get("separator.png");
	
	public static final Bitmap BBSMART_LOGO = get("bbsmart"
			+ (DEVICE_240W ? "-240" : "-320") + ".jpg");
	
	public static Bitmap get(String imageName) {
		return Bitmap.getBitmapResource("img/" + imageName);
	}
	
	// ***************** UI Skin Methods
	public static String getTitle() {
		return (DEVICE_240W ? TITLE_240 : TITLE_320);
	}
	
	public static String getScreenBackground() {
		return (DEVICE_240W ? BACKGROUND_240 : BACKGROUND_320);
	}
	
	public static Bitmap getGlassyOverlay() {
		return ImageFileUtil.getThemeImage(GLASSY_OVERLAY);
	}
	
	public static String getViewSelect() {
		return (DEVICE_240W ? VIEW_SELECT_240 : VIEW_SELECT_320);
	}
	
	public static String getAlbumSelect() {
		return (DEVICE_240W ? ALBUM_SELECT_240 : ALBUM_SELECT_320);
	}
	
	public static String getImageTracker() {
		return (DEVICE_240W ? IMAGE_TRACKER_240 : IMAGE_TRACKER_320);
	}
	
	public static String getStatusBar() {
		return (DEVICE_240W ? STATUS_BAR_240 : STATUS_BAR_320);
	}
	
	public static String getOptionChoice() {
		return OPTION_CHOICE;
	}
	
	public static String getDetailField() {
		return (DEVICE_240W ? DETAIL_FIELD_240 : DETAIL_FIELD_320);
	}
	
	public static String getListField() {
		return (DEVICE_240W ? LIST_FIELD_240 : LIST_FIELD_320);
	}
}