package com.github.csachs.rioh;

public class ImageArray {
    public Image[] array;

    public ImageArray() {

    }

    public ImageArray(int length) {
        array = new Image[length];
    }

    public ImageArray(Image... images) {
        array = images;
    }
}