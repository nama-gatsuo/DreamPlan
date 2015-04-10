package com.nama_gatsuo.dreamplan.model;

import java.io.Serializable;

/**
 * Created by nagamatsuayumu on 15/02/07.
 */
public class Task implements Serializable {
    private int taskID; // taskID
    private int projectID; // projectID
    private String name; // Taskの名前
    private String description; // Taskの説明
    private int status; // Taskの状態
    private long startDate; // Taskの開始日
    private long endDate; // Taskの終了日

    // メンバへのAccessor
    public void setTaskID(int taskID) { this.taskID = taskID; }
    public int getTaskID() { return taskID; }

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

    public void setEndDate(long taskEndDate) { this.endDate = taskEndDate; }
    public long getEndDate() { return endDate; }

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
