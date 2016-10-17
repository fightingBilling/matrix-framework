package org.matrix.framework.core.platform.im4j.domain;

import java.util.List;

public class Im4jResult {

    //原始图片地址
    private String originalImgPath;

    //图片规格
    private List<ErrorSpecific> errorSpecific;

    public String getOriginalImgPath() {
        return originalImgPath;
    }

    public void setOriginalImgPath(String originalImgPath) {
        this.originalImgPath = originalImgPath;
    }

    public List<ErrorSpecific> getErrorSpecific() {
        return errorSpecific;
    }

    public void setErrorSpecific(List<ErrorSpecific> errorSpecific) {
        this.errorSpecific = errorSpecific;
    }

}
