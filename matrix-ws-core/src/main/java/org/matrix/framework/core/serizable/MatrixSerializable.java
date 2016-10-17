/**********************************************************************
 * Copyright (c):    2014-2049 chengdu nstechs company, All rights reserved.
 * Technical Support:Chengdu nstechs company
 * Contact:          chris.qin@nstechs.com,15202879502
 **********************************************************************/

package org.matrix.framework.core.serizable;

public interface  MatrixSerializable {

    <T> byte[] serializable(T object);
    
    <T> T deSerializable(byte[] bytes);
    
}