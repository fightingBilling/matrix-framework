package org.matrix.framework.core.platform.im4j.domain;

public class ErrorSpecific {

    private String errorMessage;
    private ImgSpecific imgSpecific;

    public ErrorSpecific() {
    }

    public ErrorSpecific(String errorMessage, ImgSpecific imgSpecific) {
        this.errorMessage = errorMessage;
        this.imgSpecific = imgSpecific;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ImgSpecific getImgSpecific() {
        return imgSpecific;
    }

    public void setImgSpecific(ImgSpecific imgSpecific) {
        this.imgSpecific = imgSpecific;
    }

}
