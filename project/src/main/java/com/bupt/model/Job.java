package com.bupt.model;

/**
 * Job entity representing a TA position posted by a Module Organiser.
 */
public class Job {
    private String id;
    private String title;
    private String description;
    private String moduleName;
    private String requiredSkills; // comma-separated
    private String type;           // MODULE, INVIGILATION, OTHER
    private String postedBy;       // MO user id
    private String status;         // OPEN, CLOSED
    private String createdDate;

    public Job() {}

    // --- Getters and Setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getModuleName() { return moduleName; }
    public void setModuleName(String moduleName) { this.moduleName = moduleName; }

    public String getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(String requiredSkills) { this.requiredSkills = requiredSkills; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getPostedBy() { return postedBy; }
    public void setPostedBy(String postedBy) { this.postedBy = postedBy; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }

    public String toFileLine() {
        return String.join("|",
                safe(id), safe(title), safe(description), safe(moduleName),
                safe(requiredSkills), safe(type), safe(postedBy), safe(status), safe(createdDate));
    }

    public static Job fromFileLine(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length < 8) return null;
        Job j = new Job();
        j.setId(p[0]);
        j.setTitle(p[1]);
        j.setDescription(p[2]);
        j.setModuleName(p[3]);
        j.setRequiredSkills(p[4]);
        j.setType(p[5]);
        j.setPostedBy(p[6]);
        j.setStatus(p[7]);
        if (p.length > 8) j.setCreatedDate(p[8]);
        return j;
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }
}
