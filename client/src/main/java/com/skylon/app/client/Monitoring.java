package com.skylon.app.client;


import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;




/**
 * @author JiamingDuan
 */
public class Monitoring {

    /**
     * 最终要发送的命令
     */
    private String command;

    /**
     * 接收命令的端口
     */
    private int serverport;
    /**
     * 接收命令的host
     */
    private String serverhost;
    private int initdelay;
    private int period;
    private Date testtime;
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String STOP = "stop";
    private static final String SUCCESS = "success";
    private static final int STOP_SIGN = '#';
    private static final int TRY_TIMES = 3;
    private Socket clientSocket;
    static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);

     Monitoring(String serverhost, int serverport,int initdelay,int period) {
        //通过监视配置对象进行配置，该对象属性可以在app。properties中进行修改
        this.serverhost = serverhost;
        this.serverport = serverport;
        this.initdelay=initdelay;
        this.period=period;
    }



    void generateCommond(TestInformation testInformation) {
        //根据信息构建要发送的指令
        StringBuilder stringBuilder = new StringBuilder("!report#");
        stringBuilder.append(testInformation.getAreaid());
        stringBuilder.append("*");
        stringBuilder.append(testInformation.getAppid());
        stringBuilder.append("*");
        stringBuilder.append(testInformation.getModelid());
        stringBuilder.append("*");
        stringBuilder.append(SDF.format(testtime));
        stringBuilder.append("*");
        Date sendtime = new Date();
        stringBuilder.append(SDF.format(sendtime));
        stringBuilder.append("*");
        stringBuilder.append(testInformation.getStatus());
        stringBuilder.append("$");
        command = stringBuilder.toString();
    }

    void testconnection(TestInformation testInformation) {
        //在此进行ping测试操作，设定测试时间，设定测试结果
        //应更改为更精确的测试方式，现在仅作为测试用途
        Socket s = new Socket();
        SocketAddress add = new InetSocketAddress(testInformation.getTesthost(), testInformation.getTestport());
        try {
            testtime = new Date();
            s.connect(add, 10000);
            // 超时10秒
            testInformation.setStatus(1);
        } catch (IOException e) {
            //异常需要处理的业务逻辑
            testInformation.setStatus(2);
            logger.error("test connection failed");
        } finally {
            try {
                s.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    void sendmsg() {
        StringBuilder recvStrBuilder = new StringBuilder();
        try {
//创建连接服务器的Socket
            clientSocket = new Socket(serverhost, serverport);
//写出请求字符串
            OutputStream out = clientSocket.getOutputStream();
            out.write(command.getBytes());
            logger.info("the message send is : "+command);
            InputStream in = clientSocket.getInputStream();
            for (int c = in.read(); c != STOP_SIGN; c = in.read()) {
                recvStrBuilder.append((char) c);
            }
            String t = recvStrBuilder.toString();
            if (STOP.equals(t)) {
                System.out.println("all attemps failed");
            } else if (SUCCESS.equals(t)) {
                System.out.println("send success");
            } else {
                int times = Integer.valueOf(t);
                System.out.println(t);
                if (times <= TRY_TIMES) {
                    logger.info("retry times:" + times);
                    sendmsg();
                }
            }
            clientSocket.close();
        } catch (UnknownHostException e) {
            logger.error("server host not found");
        } catch (IOException e) {
            logger.info("can not connect to server port");
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void dojob(List<TestInformation> arrayList) {
        for (TestInformation testInformation : arrayList) {
            testconnection(testInformation);
            generateCommond(testInformation);
            sendmsg();
        }
    }

    void startsending(final List<TestInformation> arrayList) {
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("sending-pool-%d").daemon(true).build());
        executorService.scheduleAtFixedRate(new Runnable() {
            public void run() {
                dojob(arrayList);
            }
        }, initdelay, period, TimeUnit.MILLISECONDS);


    }

}