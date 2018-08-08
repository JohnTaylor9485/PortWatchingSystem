package com.skylon.server.service.ipl;

import com.skylon.server.dao.RealtimeMonitoringDao;
import com.skylon.server.service.ReceiverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;


/**
 * @author Jiaming Duan
 */
@Service
public class ReceiverServiceImpl implements ReceiverService{
    private ServerStarter serverStarter;
    @Autowired
    RealtimeMonitoringDao realtimeMonitoringDao;
    @Value("${server-port}")
    private int port;
    @Override
    public void startSystem() {

        try {
            serverStarter = new ServerStarter(realtimeMonitoringDao,port);
            serverStarter.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void dodestory() {
        serverStarter.shutdowm();
    }

}
