package com.haritonova.salbp.entity;

import java.util.ArrayList;

public class Workstation {
    private ArrayList<Integer> workList;
    private double time;
    private boolean containsManualWork;
    private ArrayList<Integer> manualWorkList;

    public Workstation() {
        workList = new ArrayList<Integer>();
        manualWorkList = new ArrayList<Integer>();
    }

    public Workstation(Workstation workstation) {
        this.workList = new ArrayList<Integer>(workstation.getWorkList().size());
        for(int i = 0; i < workstation.getWorkList().size(); i++) {
            this.workList.add(workstation.getWorkList().get(i));
        }
        this.time = workstation.getTime();
        this.containsManualWork = workstation.doesContainManualWork();
        this.manualWorkList = new ArrayList<Integer>(workstation.getManualWorkList().size());
        for(int i = 0; i < workstation.getManualWorkList().size(); i++) {
            this.manualWorkList.add(workstation.getManualWorkList().get(i));
        }
    }

    public ArrayList<Integer> getWorkList() {
        return workList;
    }

    public void setWorkList(ArrayList<Integer> workList) {
        this.workList = workList;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public boolean doesContainManualWork() {
        return containsManualWork;
    }

    public void setContainsManualWork(boolean containsManualWork) {
        this.containsManualWork = containsManualWork;
    }

    public void addWork(int work) {
        workList.add(work);
    }

    public void addWork(int work, double time, boolean isManual) {
        this.time = this.time + time;
        workList.add(work);
        if(isManual) {
            this.containsManualWork = true;
            this.manualWorkList.add(work);
        }
    }

    public ArrayList<Integer> getManualWorkList() {
        return manualWorkList;
    }

    public void setManualWorkList(ArrayList<Integer> manualWorkList) {
        this.manualWorkList = manualWorkList;
    }

    @Override
    public String toString() {
        String workString = "";
        for(int i = 0; i < workList.size(); i++) {
            workString += workList.get(i) + " ";
        }
        workString += " Mutual: ";
        for(int i = 0; i < manualWorkList.size(); i++) {
            workString += manualWorkList.get(i) + " ";
        }
        return "Workstation{" +
                "workList=" + workString +
                ", time=" + time +
                ", containsManualWork=" + containsManualWork +
                '}';
    }
}
