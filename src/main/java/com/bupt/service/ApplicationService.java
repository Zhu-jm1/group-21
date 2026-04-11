package com.bupt.service;

import com.bupt.dao.ApplicationDao;
import com.bupt.model.Application;
import java.time.LocalDate;
import java.util.List;

/**
 * Business logic for application operations.
 */
public class ApplicationService {

    private final ApplicationDao appDao = new ApplicationDao();

    public void apply(String jobId, String applicantId, String note) {
        Application app = new Application();
        app.setJobId(jobId);
        app.setApplicantId(applicantId);
        app.setStatus("PENDING");
        app.setApplyDate(LocalDate.now().toString());
        app.setNote(note);
        appDao.save(app);
    }

    public List<Application> getByApplicant(String applicantId) {
        return appDao.findByApplicantId(applicantId);
    }

    public List<Application> getByJob(String jobId) {
        return appDao.findByJobId(jobId);
    }

    public void updateStatus(String appId, String status) {
        Application app = appDao.findById(appId);
        if (app != null) {
            app.setStatus(status);
            appDao.update(app);
        }
    }

    public List<Application> getAll() {
        return appDao.findAll();
    }
}
