package org.matrix.framework.core.kafka.consumer.strategy;

/**
 * Matrix message consumer strategy.具体的业务处理逻辑应当继承这个抽象类.
 *
 * @author pankai 2015年8月17日
 */
public abstract class MatrixConsumerStrategy {

    /**
     * 处理业务的具体方法.
     *
     * @Note 由于可能会读取到重复数据,故该操作应该具有幂等性或者处理之前查重.
     * @Note 考虑取消自动提交,在业务事务完成之后手动提交.防止由于消息消费业务bug而造成消息丢失.
     */
    public abstract void process(String message, long offset);

    /**
     * 业务处理出现异常之后调用此方法.持久化消息,记录日志等.
     * 
     * @Warning 如果这里没有保存消息,或者保存消息失败.消息将会漏读.
     */
    public abstract void onException(String message, long offset, Exception exception);

    /**
     * Message process successfully, this method will be executed.
     * 
     * @param message
     * @param offset
     */
    public abstract void onSuccess(String message, long offset);
}
