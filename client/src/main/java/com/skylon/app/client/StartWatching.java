package com.skylon.app.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;
import java.lang.reflect.Type;
import java.util.List;


/**
 * @author JiamingDuan
 */
public class StartWatching extends HttpServlet implements ServletContextListener {


    @Value("${JsonString}")
    String jsonstring;
    @Value("${server_port}")
    int port;
    @Value("${server_host}")
    String host;
    @Value("${initial_delay}")
    int delay;
    @Value("${period}")
    int period;


    public void contextDestroyed(ServletContextEvent arg0) {

    }

    public void contextInitialized(ServletContextEvent arg0) {

        WebApplicationContextUtils.getRequiredWebApplicationContext(arg0.getServletContext())
                .getAutowireCapableBeanFactory().autowireBean(this);
        Gson gson = new Gson();
        Type type = new TypeToken<List<TestInformation>>() {
        }.getType();
        List<TestInformation> testInformationList = gson.fromJson(jsonstring, type);
        Monitoring monitoring = new Monitoring(host, port, delay, period);
        monitoring.startsending(testInformationList);
    }
}
