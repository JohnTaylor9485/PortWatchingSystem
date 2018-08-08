package com.skylon.server.service.ipl;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.skylon.server.dao.RealtimeMonitoringDao;
import com.skylon.server.timertask.Receiver;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.*;



/**
 * @author Jiaming Duan
 */
public class ServerStarter extends Thread {
    /**
     * 服务器IP
     */
    private static final String SERVER_IP = "localhost";

    //服务器端口号
    /**
     * 请求终结字符串
     */
    private Selector selector;
    private static ExecutorService serverThreadPool;
    private RealtimeMonitoringDao realtimeMonitoringDao;
    private static volatile boolean flag = true;
    private ServerSocketChannel server;

    ServerStarter(RealtimeMonitoringDao realtimeMonitoringDao, int port) throws IOException {

        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("server-pool-%d").build();
        serverThreadPool = new ThreadPoolExecutor(10, 10,
                6000, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024), threadFactory, new ThreadPoolExecutor.AbortPolicy());
        this.realtimeMonitoringDao = realtimeMonitoringDao;

//使用NIO需要用到ServerSocketChannel
//其中包含一个ServerSocket对象
        ServerSocketChannel serverChannel = ServerSocketChannel.open();

//创建地址对象
        InetSocketAddress localAddr = new InetSocketAddress(SERVER_IP, port);

//服务器绑定地址
        serverChannel.bind(localAddr);

//设置为非阻塞
        serverChannel.configureBlocking(false);

//注册到selector，会调用ServerSocket的accept
//我们用selector监听accept能否返回
//当调用accept可以返回时，会得到通知
//注意，是可以返回，还需要调用accept
        selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    void shutdowm() {
        try {
            flag = false;
            server.close();
            serverThreadPool.shutdownNow();
            selector.close();
        } catch (IOException e) {
            Receiver.logger.info("close thread and selector");
        }

    }

    @Override
    public void run() {
        while (flag) {
            action();
        }
    }


     void action() {

//调用select，阻塞在这里，直到有注册的channel满足条件
        try {
            selector.select();
        } catch (IOException e) {
            e.printStackTrace();
        }

//如果走到这里，有符合条件的channel
//可以通过selector.selectedKeys().iterator()拿到符合条件的迭代器
        try {
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
//处理满足条件的keys
            while (keys.hasNext()) {
//取出一个key并移除
                SelectionKey key = keys.next();
                keys.remove();
                try {
                    if (key.isAcceptable()) {
//有accept可以返回
//取得可以操作的channel
                        server = (ServerSocketChannel) key.channel();

//调用accept完成三次握手，返回与客户端可以通信的channel
                        SocketChannel channel = server.accept();

//将该channel置非阻塞
                        channel.configureBlocking(false);

//注册进selector，当可读或可写时将得到通知，select返回
                        serverThreadPool.execute(new SocketTask(channel, realtimeMonitoringDao));
                    }
                } catch (IOException e) {
//当客户端Socket关闭时，会走到这里，清理资源
                    key.cancel();

                    try {
                        key.channel().close();
                    } catch (IOException e1) {
                        Receiver.logger.error(e1);
                    }
                }

            }
        } catch (ClosedSelectorException exception) {
            Receiver.logger.info("system shut down");
        }

    }

}



