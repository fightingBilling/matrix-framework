package org.matrix.framework.core.platform.im4j.domain;

//注意这里没有实现序列化接口.
public class ImgSpecific {

    private String imgType;
    private long width;
    private long height;

    public ImgSpecific() {

    }

    public ImgSpecific(long width, long height) {
        this.width = width;
        this.height = height;
    }

    public String getImgType() {
        return imgType;
    }

    public void setImgType(String imgType) {
        this.imgType = imgType;
    }

    public long getWidth() {
        return width;
    }

    public void setWidth(long width) {
        this.width = width;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

}
