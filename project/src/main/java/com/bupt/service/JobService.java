package com.bupt.service;

import com.bupt.dao.JobDao;
import com.bupt.model.Job;
import java.time.LocalDate;
import java.util.List;

/**
 * Business logic for job operations.
 */
public class JobService {

    private final JobDao jobDao = new JobDao();

    public List<Job> getOpenJobs() {
        return jobDao.findOpenJobs();
    }

    public List<Job> getJobsByMO(String moId) {
        return jobDao.findByPostedBy(moId);
    }

    public Job getJobById(String id) {
        return jobDao.findById(id);
    }

    public void postJob(Job job) {
        job.setStatus("OPEN");
        job.setCreatedDate(LocalDate.now().toString());
        jobDao.save(job);
    }

    public void closeJob(String jobId) {
        Job job = jobDao.findById(jobId);
        if (job != null) {
            job.setStatus("CLOSED");
            jobDao.update(job);
        }
    }

    public List<Job> getAllJobs() {
        return jobDao.findAll();
    }
}
