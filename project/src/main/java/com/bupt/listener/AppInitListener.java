package com.bupt.listener;

import com.bupt.dao.FileBaseDao;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Initializes the data directory on application startup.
 */
@WebListener
public class AppInitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String dataDir = sce.getServletContext().getRealPath("/WEB-INF/data");
        FileBaseDao.initDataDir(dataDir);
        String cvDir = sce.getServletContext().getRealPath("/WEB-INF/uploads/cv");
        try {
            if (cvDir != null) {
                Files.createDirectories(Paths.get(cvDir));
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot create CV upload directory: " + cvDir, e);
        }
        System.out.println("TA Recruitment System started. Data dir: " + dataDir);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {}
}
