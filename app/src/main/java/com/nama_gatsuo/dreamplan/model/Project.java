package com.nama_gatsuo.dreamplan.model;

import java.io.Serializable;

/**
 * Created by nagamatsuayumu on 15/03/01.
 */
public class Project implements Serializable {
    private int projectID;
    private String name;
    private String description;
    private int status;
    private long startDate;
    private long endDate;
    private String imagePath;

    // メンバへのAccessor
    public void setProjectID(int projectID) { this.projectID = projectID; }
    public int getProjectID() { return projectID; }

    public void setName(String name) { this.name = name; }
    public String getName() { return name; }

    public void setDescription(String description) { this.description = description; }
    public String getDescription() { return description; }

    public void setStatus(int status) { this.status = status; }
    public int getStatus() { return status; }

    public void setStartDate(long startDate) { this.startDate = startDate; }
    public long getStartDate() { return startDate; }

    public void setEndDate(long endDate) { this.endDate = endDate; }
    public long getEndDate() { return endDate; }

    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public String getImagePath() { return imagePath; }

    // validation
    public boolean validate() {
        // 文字数制限
        if (name.length() > 21) { return false; }
        if (description.length() > 150) { return false; }

        // 負の数ははじく
        if (startDate < 0) { return false; }
        if (endDate < 0) { return false; }

        // 開始日 > 終了日の場合をはじく
        if (startDate > endDate) { return false; }

        return true;
    }
}
