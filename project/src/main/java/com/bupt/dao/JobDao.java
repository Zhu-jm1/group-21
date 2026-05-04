package com.bupt.dao;

import com.bupt.model.Job;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Data access for Job entities, stored in jobs.txt.
 */
public class JobDao extends FileBaseDao {

    private static final String FILE_NAME = "jobs.txt";

    public List<Job> findAll() {
        List<Job> jobs = new ArrayList<>();
        for (String line : readAllLines(FILE_NAME)) {
            Job j = Job.fromFileLine(line);
            if (j != null) jobs.add(j);
        }
        return jobs;
    }

    public Job findById(String id) {
        return findAll().stream().filter(j -> j.getId().equals(id)).findFirst().orElse(null);
    }

    public List<Job> findByPostedBy(String moId) {
        List<Job> result = new ArrayList<>();
        for (Job j : findAll()) {
            if (j.getPostedBy().equals(moId)) result.add(j);
        }
        return result;
    }

    public List<Job> findOpenJobs() {
        List<Job> result = new ArrayList<>();
        for (Job j : findAll()) {
            if ("OPEN".equals(j.getStatus())) result.add(j);
        }
        return result;
    }

    public void save(Job job) {
        if (job.getId() == null || job.getId().isEmpty()) {
            job.setId(UUID.randomUUID().toString().substring(0, 8));
        }
        appendLine(FILE_NAME, job.toFileLine());
    }

    public void update(Job job) {
        List<Job> all = findAll();
        List<String> lines = new ArrayList<>();
        for (Job j : all) {
            lines.add(j.getId().equals(job.getId()) ? job.toFileLine() : j.toFileLine());
        }
        writeAllLines(FILE_NAME, lines);
    }
}
