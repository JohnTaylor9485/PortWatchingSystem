package com.skylon.server.entity;

import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Jiaming Duan
 */
@Component
public class RealTimeMonitoringInformation {
    private String projectcode;
    private String programcode;
    private String parametercode;
    private Date collectiontime;
    private int status;
    private Date receivetime;

    public RealTimeMonitoringInformation() {
        projectcode = "0000";
        programcode = "0000";
        parametercode = "0000";
        collectiontime = new Date();
        status = 1;
        receivetime = new Date();
    }

    public String getProjectcode() {
        return projectcode;
    }

    public void setProjectcode(String projectcode) {
        this.projectcode = projectcode;
    }

    public String getProgramcode() {
        return programcode;
    }

    public void setProgramcode(String programcode) {
        this.programcode = programcode;
    }

    public String getParametercode() {
        return parametercode;
    }

    public void setParametercode(String parametercode) {
        this.parametercode = parametercode;
    }

    public Date getCollectiontime() {
        return collectiontime;
    }

    public void setCollectiontime(Date collectiontime) {
        this.collectiontime = collectiontime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getReceivetime() {
        return receivetime;
    }

    public void setReceivetime(Date receivetime) {
        this.receivetime = receivetime;
    }
}
