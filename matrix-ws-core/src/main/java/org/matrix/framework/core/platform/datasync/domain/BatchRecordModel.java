package org.matrix.framework.core.platform.datasync.domain;

import java.util.List;

/**
 * matrix数据同步框架的数据模型.
 * 
 * @author pankai 2015年7月19日
 */
public class BatchRecordModel<T> {

    // 起始序号
    private int startSequenceNo;

    // 结束序号
    private int endSequenceNo;

    // 具体数据
    private List<Record> records;

    private T extend;

    public T getExtend() {
        return extend;
    }

    public void setExtend(T extend) {
        this.extend = extend;
    }

    public int getStartSequenceNo() {
        return startSequenceNo;
    }

    public void setStartSequenceNo(int startSequenceNo) {
        this.startSequenceNo = startSequenceNo;
    }

    public int getEndSequenceNo() {
        return endSequenceNo;
    }

    public void setEndSequenceNo(int endSequenceNo) {
        this.endSequenceNo = endSequenceNo;
    }

    public List<Record> getRecords() {
        return records;
    }

    public void setRecords(List<Record> records) {
        this.records = records;
    }

    @Override
    public String toString() {
        return "[" + records + "]";
    }

}