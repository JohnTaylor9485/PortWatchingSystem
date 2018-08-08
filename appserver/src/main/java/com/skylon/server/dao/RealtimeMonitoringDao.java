package com.skylon.server.dao;


import com.skylon.server.entity.RealTimeMonitoringInformation;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RealtimeMonitoringDao extends Runnable{

    /**
     * 插入数据
     *
     * @param realTimeMonitoringInformation 实时监视信息对象
     * @return 返回插入数量
     */
    int insertMInformation(RealTimeMonitoringInformation realTimeMonitoringInformation);
    /**
     * 验证项目编号有效性
     *
     * @param id 项目编号
     * @return 查询到的项目编号列表
     * */
    List<String> verifyProjId(String id);
    /**
     *
     * 验证程序编号有效性
     *
     * @param id 程序编号
     * @return 查询到的程序编号列表
     *
     */
    List<String> verifyAppId(String id);
    /**
     *
     * 验证接口编号有效性
     *
     * @param id 接口编号
     * @return 查询到的接口编号列表
     *
     */
    List<String> verifyParamId(String id);

}

