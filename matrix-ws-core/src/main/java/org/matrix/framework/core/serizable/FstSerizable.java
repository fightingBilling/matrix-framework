package org.matrix.framework.core.serizable;

/**
 * 初始化FSTConfiguration很昂贵.注释掉..
 * @author pankai
 * Oct 23, 2015
 */
public class FstSerizable implements MatrixSerializable {

    @Override
    public <T> byte[] serializable(T object) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> T deSerializable(byte[] bytes) {
        // TODO Auto-generated method stub
        return null;
    }

    //    public static final FSTConfiguration CONFIGURATION = FSTConfiguration.createFastBinaryConfiguration();
    //
    //    @Override
    //    public <T> byte[] serializable(T object) {
    //        return CONFIGURATION.asByteArray(object);
    //    }
    //
    //    @SuppressWarnings("unchecked")
    //    @Override
    //    public <T> T deSerializable(byte[] bytes) {
    //        return (T) CONFIGURATION.asObject(bytes);
    //    }

}