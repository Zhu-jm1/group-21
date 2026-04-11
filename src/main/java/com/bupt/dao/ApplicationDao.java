package com.bupt.dao;

import com.bupt.model.Application;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Data access for Application entities, stored in applications.txt.
 */
public class ApplicationDao extends FileBaseDao {

    private static final String FILE_NAME = "applications.txt";

    public List<Application> findAll() {
        List<Application> apps = new ArrayList<>();
        for (String line : readAllLines(FILE_NAME)) {
            Application a = Application.fromFileLine(line);
            if (a != null) apps.add(a);
        }
        return apps;
    }

    public Application findById(String id) {
        return findAll().stream().filter(a -> a.getId().equals(id)).findFirst().orElse(null);
    }

    public List<Application> findByApplicantId(String applicantId) {
        List<Application> result = new ArrayList<>();
        for (Application a : findAll()) {
            if (a.getApplicantId().equals(applicantId)) result.add(a);
        }
        return result;
    }

    public List<Application> findByJobId(String jobId) {
        List<Application> result = new ArrayList<>();
        for (Application a : findAll()) {
            if (a.getJobId().equals(jobId)) result.add(a);
        }
        return result;
    }

    public void save(Application app) {
        if (app.getId() == null || app.getId().isEmpty()) {
            app.setId(UUID.randomUUID().toString().substring(0, 8));
        }
        appendLine(FILE_NAME, app.toFileLine());
    }

    public void update(Application app) {
        List<Application> all = findAll();
        List<String> lines = new ArrayList<>();
        for (Application a : all) {
            lines.add(a.getId().equals(app.getId()) ? app.toFileLine() : a.toFileLine());
        }
        writeAllLines(FILE_NAME, lines);
    }
}
