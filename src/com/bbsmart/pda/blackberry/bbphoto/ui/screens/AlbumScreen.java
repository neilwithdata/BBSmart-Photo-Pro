package com.bbsmart.pda.blackberry.bbphoto.ui.screens;

import com.bbsmart.pda.blackberry.bbphoto.models.Album;
import com.bbsmart.pda.blackberry.bbphoto.models.AlbumList;
import com.bbsmart.pda.blackberry.bbphoto.ui.customfields.BBPhotoDialog;
import com.bbsmart.pda.blackberry.bbphoto.ui.customfields.BBPhotoScreen;
import com.bbsmart.pda.blackberry.bbphoto.ui.customfields.StyledButtonField;
import com.bbsmart.pda.blackberry.bbphoto.util.UiUtil;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.container.HorizontalFieldManager;

// TODO: Perform testing on this screen's operation as confidence is not 100%
public final class AlbumScreen extends BBPhotoScreen {
	private Album album = null;
	
	private BasicEditField albumName;
	private CheckboxField isPrivate;
	
	// Constructor for a New Album Screen
	public AlbumScreen() {
		setTitle(UiUtil.NEW_ALBUM_TITLE);
		
		add(new LabelField("Album Name:"));
		albumName = new BasicEditField("", "", 25, BasicEditField.FILTER_DEFAULT | BasicEditField.NO_NEWLINE);
		add(albumName);
		isPrivate = new CheckboxField("Private: ", false);
		add(isPrivate);
		
		HorizontalFieldManager hfm = new HorizontalFieldManager(HorizontalFieldManager.FIELD_HCENTER);
		hfm.add(new StyledButtonField("Create", StyledButtonField.SIZE_MID, 0, getFont().derive(Font.BOLD)) {
			protected boolean trackwheelClick(int status, int time) {
				if(!AlbumList.getInstance().newAlbum(albumName.getText())) {
					BBPhotoDialog.alert("Album name already exists");
					return true;
				}
				AlbumList.getInstance().getAlbum(albumName.getText()).setPrivate(isPrivate.getChecked());
				dataChange();
				close();
				return true;
			}
		});
		add(hfm);
	}
	
	// Constructor for Album Properties Screen
	public AlbumScreen(String name) {
		setTitle(UiUtil.ALBUM_PROP_TITLE);
		
		album = AlbumList.getInstance().getAlbum(name);
		
		add(new LabelField("Album Name:"));
		albumName = new BasicEditField("", album.getName(), 25, BasicEditField.FILTER_DEFAULT | BasicEditField.NO_NEWLINE);
		add(albumName);
		if(!name.equals(AlbumList.UNSORTED_ALBUM_NAME)) {
			isPrivate = new CheckboxField("Private: ", album.isPrivate());
			add(isPrivate);
			isPrivate.setPadding(1, 0, 15, 5);
		} else {
			albumName.setPadding(1, 0, 15, 5);
		}
		add(new LabelField("Number of images: " + album.getSize()));
		int fileSize = album.getFileSize();
		if(fileSize > 1024) {
			add(new LabelField("Album Size: " + (((double)((long)((((double)fileSize )/1024)*100)))/100) + " MB"));
		} else {
			add(new LabelField("Album Size: " + fileSize + " KB"));
		}
	}
	
	protected void paint(Graphics g) {
		super.paint(g);
		if(album != null) {
			int y = albumName.getTop() + albumName.getHeight() + (g.getFont().getHeight()+6);
			if(isPrivate != null) {
				y = isPrivate.getTop() + isPrivate.getHeight() + (g.getFont().getHeight()+6);
			}
			g.drawLine(0, y-2, Graphics.getScreenWidth(), y-2);
			g.drawLine(0, y-1, Graphics.getScreenWidth(), y-1);
		}
	}
	
	private void dataChange() {
		AlbumManagementScreen.dirty = true;
		AlbumViewScreen.dirty = true;
		AlbumViewScreen.dataValid = false;
	}
	
	private void renameAlbum() {
		if(album.getName().equals(AlbumList.UNSORTED_ALBUM_NAME)) {
			AlbumList.getInstance().getAlbum(album.getName()).setName(albumName.getText());
			AlbumList.UNSORTED_ALBUM_NAME = albumName.getText();
		} else {
			AlbumList.getInstance().getAlbum(album.getName()).setName(albumName.getText());
		}
	}
	
	public boolean onClose() {
		if(album != null) {
			if(!album.getName().equals(albumName.getText())) {
				if(AlbumList.getInstance().containsAlbum(albumName.getText())) {
					BBPhotoDialog.alert("Album name already exists");
					return true;
				}
				renameAlbum();
				dataChange();
			}
			if(isPrivate != null) {
				if(AlbumList.getInstance().getAlbum(album.getName()).isPrivate() != isPrivate.getChecked()) {
					AlbumList.getInstance().getAlbum(album.getName()).setPrivate(isPrivate.getChecked());
					dataChange();
				}
			}
		}
		close();
		return true;
	}
}