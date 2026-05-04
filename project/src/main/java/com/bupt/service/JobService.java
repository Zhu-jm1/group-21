package com.bupt.service;

import com.bupt.dao.JobDao;
import com.bupt.model.Job;
import com.bupt.model.User;
import com.bupt.util.AIService;
import com.bupt.util.MatchUtil;
import java.time.LocalDate;
import java.util.List;

/**
 * Business logic for job operations.
 */
public class JobService {

    private final JobDao jobDao = new JobDao();
    private final AIService aiService = new AIService();

    public List<Job> getOpenJobs() {
        return jobDao.findOpenJobs();
    }

    public List<Job> getOpenJobsForUser(User user) {
        List<Job> jobs = jobDao.findOpenJobs();
        if (user != null && "TA".equals(user.getRole())) {
            for (Job job : jobs) {
                job.setMatchScore(calculateMatchScore(user, job));
            }
            jobs.sort((a, b) -> Integer.compare(b.getMatchScore(), a.getMatchScore()));
        }
        return jobs;
    }

    public int calculateMatchScore(User user, Job job) {
        if (aiService.isEnabled()) {
            return aiService.calculateMatchScore(user, job);
        }
        return MatchUtil.calculateMatchScore(user, job);
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
