package com.skylon.Appmonitor.service.impl;

import com.skylon.Appmonitor.dao.SearchOptionDao;
import com.skylon.Appmonitor.entity.*;
import com.skylon.Appmonitor.service.INameInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
;
import java.util.Date;
import java.util.List;

@Service
public class NameInfoipl implements INameInfo {
    @Autowired
    SearchOptionDao searchOptionDao;


    @Override
    public List<Project> tableinfoabc() {
        return searchOptionDao.tableinfoabc();
    }

    @Override
    public String findStatus(String proj, String app, String param) {
        String flag = "OK";
        List<RealTimeMonitoringInformation> status = searchOptionDao.findStatus(proj, app, param);
        if (status.isEmpty()) {
            flag = "ERROR0";
        } else {
            System.out.println(status.get(0).getStatus());
            if (status.get(0).getStatus() != 1) {
                flag = "ERROR1";
            } else {
                Date d1 = new Date();
                Date d2 = status.get(0).getReceiveTime();
                double hour = (d1.getTime() - d2.getTime()) / 1000 / 60 / 60 ;
                System.out.println(hour);
                if (hour > 23.00) {
                    flag = "ERROR2";
                }
            }
        }
        return flag;
    }

    @Override
    public List<CompleteInformation> findAllStatus(String proj, String app, String param) {
        return searchOptionDao.findAllStatus(proj, app, param);
    }
}
