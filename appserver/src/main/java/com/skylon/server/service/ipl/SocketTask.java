package com.skylon.server.service.ipl;

import com.skylon.server.dao.RealtimeMonitoringDao;
import com.skylon.server.entity.RealTimeMonitoringInformation;
import com.skylon.server.timertask.Receiver;


import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;


/**
 * @author Jiaming Duan
 */
public class SocketTask extends Thread implements Runnable {
    private RealtimeMonitoringDao realtimeMonitoringDao;
    private RealTimeMonitoringInformation realTimeMonitoringInformation = new RealTimeMonitoringInformation();
    private SocketChannel socketChannel;
    private final static SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static int count = 0;
    private static final String SUCCESS = "1$";
    private static final int TRY_TIMES = 3;
    private static final int STATUS_LOCATION = 5;

     SocketTask(SocketChannel socketChannel, RealtimeMonitoringDao realtimeMonitoringDao) {
        this.socketChannel = socketChannel;
        this.realtimeMonitoringDao = realtimeMonitoringDao;


    }

    private String readMsg(SocketChannel channel) throws IOException {
        StringBuffer stringBuffer = new StringBuffer();
        ByteBuffer buf = ByteBuffer.allocate(1024);
        int len = 0;
        while ((len = channel.read(buf)) > 0) {
            buf.flip();
            byte[] bytes = new byte[1024];
            buf.get(bytes, 0, len);
            stringBuffer.append(new String(bytes, 0, len));
            Receiver.logger.info("the message is: "+new String(bytes, 0, len));
        }
        return stringBuffer.toString();
    }

    @Override
    public synchronized void run() {
        try {
            // 得到socket连接
            // 得到客户端发来的消息

            String mess = readMsg(socketChannel);
            boolean b = mess.matches("^(!report#)(\\d{4})(\\*)(\\d{4})(\\*)(\\d{4})(\\*)((((19|20)\\d{2})-(0?[13-9]|1[012])-(0?[1-9]|[12]\\d|30))|(((19|20)\\d{2})-(0?[13578]|1[02])-31)|(((19|20)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|((((19|20)([13579][26]|[2468][048]|0[48]))|(2000))-0?2-29))\\s(20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d(\\*)((((19|20)\\d{2})-(0?[13-9]|1[012])-(0?[1-9]|[12]\\d|30))|(((19|20)\\d{2})-(0?[13578]|1[02])-31)|(((19|20)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|((((19|20)([13579][26]|[2468][048]|0[48]))|(2000))-0?2-29))\\s(20|21|22|23|[0-1]\\d):[0-5]\\d:[0-5]\\d(\\*)([1-2])(\\$)$");
            if (b) {

                String[] ms = mess.split("#");
                String[] result = ms[1].split("[*]");
                //切分获得的消息
                boolean flag1 = realtimeMonitoringDao.verifyProjId(result[0]).size() == 1;
                boolean flag2 = realtimeMonitoringDao.verifyAppId(result[1]).size() == 1;
                boolean flag3 = realtimeMonitoringDao.verifyParamId(result[2]).size() == 1;
                if (flag1 && flag2 && flag3) {
                    socketChannel.write(ByteBuffer.wrap("success#".getBytes()));
                    //获取一个输出流，向服务端发送信息
                    realTimeMonitoringInformation.setProjectcode(result[0]);
                    realTimeMonitoringInformation.setProgramcode(result[1]);
                    realTimeMonitoringInformation.setParametercode(result[2]);
                    realTimeMonitoringInformation.setCollectiontime(SDF.parse(result[3]));
                    realTimeMonitoringInformation.setReceivetime(SDF.parse(result[4]));
                    if (SUCCESS.equals(result[STATUS_LOCATION])) {
                        realTimeMonitoringInformation.setStatus(1);
                    } else {
                        realTimeMonitoringInformation.setStatus(2);
                    }
                    //新建信息对象，进行赋值
                    realtimeMonitoringDao.insertMInformation(realTimeMonitoringInformation);
                    //写入到数据库
                    socketChannel.close();
                    Receiver.logger.info("close socket");
                } else {
                    Receiver.logger.error("code not in list");

                    retry();
                }
            } else {
                Receiver.logger.error("client send wrong data");
                retry();

            }

            sleep(1000);
        } catch (IOException e) {
            Receiver.logger.info("client send nothing");
            try {
                retry();
            } catch (IOException e1) {
                e1.printStackTrace();
                Receiver.logger.info("client error");
            }
        } catch (ParseException e) {
            Receiver.logger.info("date parse failed");

        }catch (InterruptedException ex){
            ex.printStackTrace();
            Receiver.logger.info("thread stop");
        }
        currentThread().interrupt();
    }

    private synchronized void retry() throws IOException {
        if (count >= TRY_TIMES) {
            count = 0;
            socketChannel.write(ByteBuffer.wrap("stop#".getBytes()));
            Receiver.logger.info("client failed send correct data");
            socketChannel.close();
            Receiver.logger.info("close socket");
        } else {
            socketChannel.write(ByteBuffer.wrap((String.valueOf(++count)+"#").getBytes()));
        }

    }

}