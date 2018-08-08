package com.skylon.server.timertask;


import com.skylon.server.service.ReceiverService;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;

/**
 * @author Jiaming Duan
 */
public class Receiver extends HttpServlet implements ServletContextListener {
    public static Logger logger = LogManager.getLogger(LogManager.ROOT_LOGGER_NAME);


    @Autowired
    ReceiverService receiverService;
    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        receiverService.dodestory();
    }


    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        WebApplicationContextUtils.getRequiredWebApplicationContext(arg0.getServletContext())
                .getAutowireCapableBeanFactory().autowireBean(this);
        logger.info("-----------------汇报监听已启动--------------------------");
        receiverService.startSystem();
    }
}

