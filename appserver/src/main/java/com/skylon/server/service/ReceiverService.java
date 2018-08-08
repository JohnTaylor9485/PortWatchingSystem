package com.skylon.server.service;

/**
 * @author Jiaming Duan
 */
public interface ReceiverService {
    /**
     * 开始监听
     * */
    void startSystem();

    /**
     * 关闭监听和线程池
     */
    void dodestory();

}