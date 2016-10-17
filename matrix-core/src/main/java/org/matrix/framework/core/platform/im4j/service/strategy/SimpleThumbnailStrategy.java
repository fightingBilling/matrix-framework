package org.matrix.framework.core.platform.im4j.service.strategy;

import net.coobird.thumbnailator.Thumbnails;

public class SimpleThumbnailStrategy implements ThumbnailStrategy {

    @Override
    public void thumbnailImmage(String originalImgPath, double imgPdi, long width, long height, String destImgPath) throws Exception {
        Thumbnails.of(originalImgPath).size((int) width, (int) height).outputQuality(1.0F).toFile(destImgPath);
    }

}
