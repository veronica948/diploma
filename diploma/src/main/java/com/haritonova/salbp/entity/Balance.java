package com.haritonova.salbp.entity;

import java.util.ArrayList;
import java.util.Collections;

public class Balance implements Cloneable, Comparable {
    private ArrayList<Workstation> workstationList;
    private BalanceType type;
    private double time;
    private int amountOfEmptyWorkstations;
    private int amountOfWorkstations;
    private double goalFunction;
    private double radius = -1;
    private ArrayList<Integer> mostLoadedWorkstations;
    private ArrayList<ArrayList<Integer>> w;

    public Balance(int amountOfWorkstations) {
        this.amountOfWorkstations = amountOfWorkstations;
        this.amountOfEmptyWorkstations = amountOfWorkstations;
        workstationList = new ArrayList<Workstation>(amountOfWorkstations);
        for(int i = 0; i < amountOfWorkstations; i++) {
            workstationList.add(new Workstation());
        }
        this.mostLoadedWorkstations = new ArrayList<Integer>();
    }

    public Balance(Balance balance) {
        this.amountOfWorkstations = balance.getAmountOfWorkstations();
        workstationList = new ArrayList<Workstation>(amountOfWorkstations);
        for(int i = 0; i < amountOfWorkstations; i++) {
            workstationList.add(new Workstation(balance.getWorkstationList().get(i)));
        }
        this.time = balance.getTime();
        this.amountOfEmptyWorkstations = balance.getAmountOfEmptyWorkstations();
        this.mostLoadedWorkstations = new ArrayList<Integer>(balance.getMostLoadedWorkstations().size());
        for(int i = 0; i < balance.getMostLoadedWorkstations().size(); i++) {
            mostLoadedWorkstations.add(balance.getMostLoadedWorkstations().get(i));
        }
    }

    public ArrayList<Workstation> getWorkstationList() {
        return workstationList;
    }

    public BalanceType getType() {
        return type;
    }

    public double getTime() {
        return time;
    }

    public int getAmountOfEmptyWorkstations() {
        return amountOfEmptyWorkstations;
    }

    public int getAmountOfWorkstations() {
        return amountOfWorkstations;
    }

    public double getGoalFunction() {
        return goalFunction;
    }

    public double getRadius() {
        return radius;
    }

    public void setWorkstationList(ArrayList<Workstation> workstationList) {
        this.workstationList = workstationList;
    }

    public void setType(BalanceType type) {
        this.type = type;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public void setAmountOfEmptyWorkstations(int amountOfEmptyWorkstations) {
        this.amountOfEmptyWorkstations = amountOfEmptyWorkstations;
    }

    public void setAmountOfWorkstations(int amountOfWorkstations) {
        this.amountOfWorkstations = amountOfWorkstations;
    }

    public void setGoalFunction() {
        this.goalFunction = this.amountOfWorkstations*this.time;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void addWork(int index, int work, double time, boolean isManual) {
        Workstation workstation = this.workstationList.get(index);
        if(workstation.getWorkList().isEmpty()) {
            this.amountOfEmptyWorkstations--;
        }
        workstation.addWork(work, time, isManual);
        if(workstation.getTime() > this.time) {
            this.mostLoadedWorkstations.clear();
            this.mostLoadedWorkstations.add(index + 1);
            this.time = workstation.getTime();
        } else {
            if(workstation.getTime() == this.time && !mostLoadedWorkstations.contains(index + 1)) {
                this.mostLoadedWorkstations.add(index + 1);
            }
        }
    }

    @Override
    public String toString() {
        String workString = "";
        for(int i = 0; i < workstationList.size(); i++) {
            workString += workstationList.get(i) + " ";
        }
        String mostLoadedWorkstationsStr = "";
        for(int i = 0; i < mostLoadedWorkstations.size(); i++) {
            mostLoadedWorkstationsStr += mostLoadedWorkstations.get(i) + " ";
        }
        return "Balance{" +
                "workstationList=" + workString +
                ", type=" + type +
                ", time=" + time +
                ", amountOfEmptyWorkstations=" + amountOfEmptyWorkstations +
                ", mostLoadedWorkstation=" + mostLoadedWorkstationsStr +
                ", amountOfWorkstations=" + amountOfWorkstations +
                ", goalFunction=" + goalFunction +
                ", radius=" + radius +
                '}';
    }

    public int compareTo(Object o) {
        if(this.time > ((Balance)o).getTime()) {
            return 1;
        } else {
            if(this.time < ((Balance)o).getTime()) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    public void setGoalFunction(double goalFunction) {
        this.goalFunction = goalFunction;
    }

    public ArrayList<Integer> getMostLoadedWorkstations() {
        return mostLoadedWorkstations;
    }

    public void setMostLoadedWorkstations(ArrayList<Integer> mostLoadedWorkstations) {
        this.mostLoadedWorkstations = mostLoadedWorkstations;
    }

    public ArrayList<ArrayList<Integer>> findManualMostLoaded() {
        w = new ArrayList<ArrayList<Integer>>(mostLoadedWorkstations.size());
        for(int i = 0; i < mostLoadedWorkstations.size(); i++) {
            Workstation workstation = workstationList.get(mostLoadedWorkstations.get(i) - 1);
            if(workstation.doesContainManualWork()) {
                w.add(workstation.getManualWorkList());
            } else {
                w.add(null);
            }
        }
        return w;
    }

    public ArrayList<ArrayList<Integer>> getW() {
        return w;
    }

    public void setW(ArrayList<ArrayList<Integer>> w) {
        this.w = w;
    }
}
