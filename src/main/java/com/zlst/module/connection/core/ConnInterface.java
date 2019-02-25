package com.zlst.module.connection.core;

/**
 * @author 170137 2018-12-25
 */
public interface ConnInterface <T> {

    /**
     * 异步发送数据
     * @param t
     * @return 发送成功返回 true
     */
    public boolean send(T t);

    /**
     * 同步发送数据
     * @param t
     * @return
     */
    public T sendSync(T t);

    /**
     * 获取连接id
     * @return
     */
    public String getId();

    /**
     * 获取全局唯一链接码
     * @return
     */
    String getUniqueCode();

    /**
     * 打开连接
     * @return
     */
    ConnInterface open();

    /**
     * 关闭连接
     * @return
     */
    boolean close();

}
