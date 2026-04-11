package com.bupt.model;

/**
 * Application entity representing a TA's application for a job.
 */
public class Application {
    private String id;
    private String jobId;
    private String applicantId; // TA user id
    private String status;      // PENDING, ACCEPTED, REJECTED
    private String applyDate;
    private String note;

    public Application() {}

    // --- Getters and Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }

    public String getApplicantId() { return applicantId; }
    public void setApplicantId(String applicantId) { this.applicantId = applicantId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getApplyDate() { return applyDate; }
    public void setApplyDate(String applyDate) { this.applyDate = applyDate; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String toFileLine() {
        return String.join("|",
                safe(id), safe(jobId), safe(applicantId), safe(status), safe(applyDate), safe(note));
    }

    public static Application fromFileLine(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length < 5) return null;
        Application a = new Application();
        a.setId(p[0]);
        a.setJobId(p[1]);
        a.setApplicantId(p[2]);
        a.setStatus(p[3]);
        a.setApplyDate(p[4]);
        if (p.length > 5) a.setNote(p[5]);
        return a;
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }
}
