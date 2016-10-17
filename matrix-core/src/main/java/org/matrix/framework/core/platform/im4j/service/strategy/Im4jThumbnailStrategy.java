package org.matrix.framework.core.platform.im4j.service.strategy;

import org.im4java.core.ConvertCmd;
import org.im4java.core.GMOperation;

/**
 * 此策略需要安装GraphicsMagick
 * @author pankai
 * 2016年4月7日
 */
public class Im4jThumbnailStrategy implements ThumbnailStrategy {

    @Override
    public void thumbnailImmage(String originalImgPath, double imgPdi, long width, long height, String destImgPath) throws Exception {
        final GMOperation op = new GMOperation();
        op.addImage(originalImgPath);
        op.quality(imgPdi);
        op.addRawArgs("-resize", width + "x" + height);
        op.addRawArgs("-gravity", "center");
        op.addImage(destImgPath);
        ConvertCmd convert = new ConvertCmd(true);
        convert.run(op);
    }
}
