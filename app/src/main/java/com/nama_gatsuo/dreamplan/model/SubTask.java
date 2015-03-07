package com.nama_gatsuo.dreamplan.model;

/**
 * Created by nagamatsuayumu on 15/02/07.
 */
public class SubTask extends Task {
    private int subTaskID;

    // メンバへのAccessor
    public void setSubTaskID(int subTaskID) { this.subTaskID = subTaskID; }
    public int getSubTaskID() { return this.subTaskID; }

}
