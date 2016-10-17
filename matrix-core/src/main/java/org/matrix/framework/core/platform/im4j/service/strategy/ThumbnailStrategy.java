package org.matrix.framework.core.platform.im4j.service.strategy;

public interface ThumbnailStrategy {

    void thumbnailImmage(String originalImgPath, double imgPdi, long width, long height, String destImgPath) throws Exception;

}
