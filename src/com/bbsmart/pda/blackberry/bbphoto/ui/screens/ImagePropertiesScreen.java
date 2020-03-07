package com.bbsmart.pda.blackberry.bbphoto.ui.screens;

import net.rim.device.api.io.MIMETypeAssociations;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;
import net.rim.device.api.ui.container.VerticalFieldManager;

import com.bbsmart.pda.blackberry.bbphoto.models.Album;
import com.bbsmart.pda.blackberry.bbphoto.models.AlbumList;
import com.bbsmart.pda.blackberry.bbphoto.models.AlbumPicture;
import com.bbsmart.pda.blackberry.bbphoto.models.GeneralOptions;
import com.bbsmart.pda.blackberry.bbphoto.ui.customfields.BBPhotoObjectChoiceField;
import com.bbsmart.pda.blackberry.bbphoto.ui.customfields.BBPhotoScreen;
import com.bbsmart.pda.blackberry.bbphoto.ui.customfields.SpacerField;
import com.bbsmart.pda.blackberry.bbphoto.ui.customfields.ThumbnailField;
import com.bbsmart.pda.blackberry.bbphoto.util.UiUtil;

public final class ImagePropertiesScreen extends BBPhotoScreen {
	private AlbumPicture p;
	
	private BBPhotoObjectChoiceField albums;
	private BasicEditField fileName;
	private CheckboxField isPrivate;
	private BasicEditField note;
	
	public ImagePropertiesScreen(String album, String picture) {
		setTitle(UiUtil.IMG_PROP_TITLE);
		
		p = AlbumList.getInstance().getAlbum(album).getPicture(picture);
		
		HorizontalFieldManager albumHFM = new HorizontalFieldManager() {
			protected void onFocus(int direction) {
				invalidate();
				super.onFocus(direction);
			}
			protected void onUnfocus() {
				invalidate();
				super.onUnfocus();
			}
		};
		
		albums = new BBPhotoObjectChoiceField("Album: ", AlbumList.getInstance().listAlbums(), album);
		fileName = new BasicEditField("File Name: ", p.toString(), BasicEditField.DEFAULT_MAXCHARS, BasicEditField.FILTER_FILENAME);
		isPrivate = new CheckboxField("Private", p.isPrivate());
		note = new BasicEditField("Note: ", p.getNote(), 50, BasicEditField.FILTER_DEFAULT | BasicEditField.NO_NEWLINE);
		
		String mimeType = MIMETypeAssociations.getMIMEType(p.getPath());
		
		albumHFM.add(albums);
		add(albumHFM);
		add(fileName);
		add(isPrivate);
		add(note);
		note.setPadding(1, 0, 10, 5);
		
		
		VerticalFieldManager vfm = new VerticalFieldManager(VerticalFieldManager.FIELD_VCENTER);
		vfm.add(new LabelField("File Type: " + mimeType.substring(mimeType.lastIndexOf('/')+1).toUpperCase()));
		vfm.add(new LabelField("File Size: " + p.getFileSize() + " KB"));
		vfm.add(new LabelField("Resolution: " + p.getImageWidth() + "x" + p.getImageHeight()));
		
		HorizontalFieldManager hfm = new HorizontalFieldManager();
		hfm.add(vfm);
		int space = Graphics.getScreenWidth()-80-getFont().getAdvance("Resolution: 1###x1###");
		hfm.add(new SpacerField(SpacerField.MODE_HORIZ, space));
		hfm.add(new ThumbnailField(p, ThumbnailField.NON_FOCUSABLE | ThumbnailField.FIELD_VCENTER));
		add(hfm);
		
		add(new LabelField("Path: " + p.getPath()));
	}
	
	protected void paint(Graphics g) {
		super.paint(g);
		int y = note.getTop() + note.getHeight() + 21; // Width of note + offset from title and separator
		g.drawLine(0, y-2, Graphics.getScreenWidth(), y-2);
		g.drawLine(0, y-1, Graphics.getScreenWidth(), y-1);
	}
	
	public boolean onClose() {
		if(!p.toString().equals(fileName.getText())) {
			if(p.setName(fileName.getText())) {
				AlbumViewScreen.dataValid = false;
			}
		}
		
		if(p.isPrivate() != isPrivate.getChecked()) {
			p.setPrivate(isPrivate.getChecked());
			AlbumViewScreen.dataValid = false;
			if(!GeneralOptions.getInstance().privacy_ShowHide) {
				AlbumViewScreen.dirty = true;
			}
		}

		if(!p.getNote().equals(note.getText())) {
			p.setNote(note.getText());
			AlbumViewScreen.dataValid = false;
		}

		Album a = AlbumList.getInstance().getAlbum((String)albums.getChoice(albums.getSelectedIndex()));
		if(!a.containsPicture(p.getPath())) {
			AlbumList.getInstance().deletePicture(p.getPath());
			a.addPicture(p);
			AlbumViewScreen.dataValid = false;
			AlbumViewScreen.dirty= true;
		}
		close();
		return true;
	}
}