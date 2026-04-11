package com.bupt.listener;

import com.bupt.dao.FileBaseDao;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * Initializes the data directory on application startup.
 */
@WebListener
public class AppInitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String dataDir = sce.getServletContext().getRealPath("/WEB-INF/data");
        FileBaseDao.initDataDir(dataDir);
        System.out.println("TA Recruitment System started. Data dir: " + dataDir);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {}
}
