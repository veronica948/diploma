package com.haritonova.salbp.entity;

import java.util.ArrayList;

/**
 * Created by Veronica on 1/22/2017.
 */
public class Workstation {
    private ArrayList<Integer> workList;
    private double time;
    private boolean containMutualWork;
    private ArrayList<Integer> mutualWorkList;

    public Workstation() {
        workList = new ArrayList<Integer>();
        mutualWorkList = new ArrayList<Integer>();
    }

    public Workstation(Workstation workstation) {
        this.workList = new ArrayList<Integer>(workstation.getWorkList().size());
        for(int i = 0; i < workstation.getWorkList().size(); i++) {
            this.workList.add(workstation.getWorkList().get(i));
        }
        this.time = workstation.getTime();
        this.containMutualWork = workstation.isContainMutualWork();
        this.mutualWorkList = new ArrayList<Integer>(workstation.getMutualWorkList().size());
        for(int i = 0; i < workstation.getMutualWorkList().size(); i++) {
            this.mutualWorkList.add(workstation.getMutualWorkList().get(i));
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

    public boolean isContainMutualWork() {
        return containMutualWork;
    }

    public void setContainMutualWork(boolean containMutualWork) {
        this.containMutualWork = containMutualWork;
    }

    public void addWork(int work) {
        workList.add(work);
    }

    public void addWork(int work, double time, boolean isMutual) {
        this.time = this.time + time;
        workList.add(work);
        if(isMutual) {
            this.containMutualWork = true;
            this.mutualWorkList.add(work);
        }
    }

    public ArrayList<Integer> getMutualWorkList() {
        return mutualWorkList;
    }

    public void setMutualWorkList(ArrayList<Integer> mutualWorkList) {
        this.mutualWorkList = mutualWorkList;
    }

    @Override
    public String toString() {
        String workString = "";
        for(int i = 0; i < workList.size(); i++) {
            workString += workList.get(i) + " ";
        }
        workString += " Mutual: ";
        for(int i = 0; i < mutualWorkList.size(); i++) {
            workString += mutualWorkList.get(i) + " ";
        }
        return "Workstation{" +
                "workList=" + workString +
                ", time=" + time +
                ", containMutualWork=" + containMutualWork +
                '}';
    }
}
