package com.skylon.app.client;

import java.io.Serializable;

/**
 * @author JiamingDuan
 */
public class TestInformation implements Serializable {
    /**
     * 区域代码
     */
    private String areaid;
    /**
     * 程序代码
     */
    private String appid;
    /**
     * 模块代码
     */
    private String modelid;
    /**
     * 需要测试的端口
     */
    private int testport;
    /**
     * 需要测试的host
     */
    private String testhost;
    /**
     * 连通状态，1代表接通，2代表失败
     */
    private int status;

    public TestInformation(String areaid, String appid, String modelid, int testport, String testhost)  {
        this.areaid = areaid;
        this.appid = appid;
        this.modelid = modelid;
        this.testport = testport;
        this.testhost = testhost;
        this.status=2;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTestport() {
        return testport;
    }

    public void setTestport(int testport) {
        this.testport = testport;
    }

    public String getTesthost() {
        return testhost;
    }

    public void setTesthost(String testhost) {
        this.testhost = testhost;
    }

    public String getAreaid() {
        return areaid;
    }

    public void setAreaid(String areaid) {
        this.areaid = areaid;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getModelid() {
        return modelid;
    }

    public void setModelid(String modelid) {
        this.modelid = modelid;
    }
}
