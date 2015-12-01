package gui;

import javafx.scene.image.Image;

public class Piece extends javafx.scene.image.ImageView {

	private Image backImage;
	private Image faceImage;
	private boolean faceUp = false;
	private String owner = "table!";

	public Piece(String faceImageLoc, String backImageLoc) {
		backImage = new Image(backImageLoc);
		faceImage = new Image(faceImageLoc);
		this.setImage(backImage);
	}
	
	public Piece(String faceImageLoc) {
		backImage = new Image(faceImageLoc);
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

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public boolean isFaceUp() {
		return faceUp;
	}

	public void resetOwner() {
		this.owner = "table!";
	}

}
