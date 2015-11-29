package gui;

import javafx.scene.image.Image;

public class Card extends javafx.scene.image.ImageView {

	private Image backImage;
	private Image faceImage;
	private boolean faceUp = false;
	
	public Card (String faceImageLoc, String backImageLoc) {
		backImage = new Image(backImageLoc);
		faceImage = new Image(faceImageLoc);
		this.setImage(backImage);
	}
	
	public void flipImage() {
		if (faceUp == true) {
			this.setImage(backImage);
			faceUp = false;
		} else {
			this.setImage(faceImage);
			faceUp = true;
		}
	}
	
}
