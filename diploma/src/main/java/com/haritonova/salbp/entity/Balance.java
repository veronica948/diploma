package com.haritonova.salbp.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Created by Veronica on 1/22/2017.
 */
public class Balance implements Cloneable, Comparable {
    private ArrayList<Workstation> workstationList;
    private BalanceType type;
    private double time;
    private int amountOfEmptyWorkstations;
    private int mostLoadedWorkstation;
    private int amountOfWorkstations;
    private double goalFunction;
    private double radius;

    public Balance(int amountOfWorkstations) {
        this.amountOfWorkstations = amountOfWorkstations;
        this.amountOfEmptyWorkstations = amountOfWorkstations;
        workstationList = new ArrayList<Workstation>(amountOfWorkstations);
        for(int i = 0; i < amountOfWorkstations; i++) {
            workstationList.add(new Workstation());
        }
    }

    public Balance(Balance balance) {
        this.amountOfWorkstations = balance.getAmountOfWorkstations();
        workstationList = new ArrayList<Workstation>(amountOfWorkstations);
        for(int i = 0; i < amountOfWorkstations; i++) {
            workstationList.add(new Workstation(balance.getWorkstationList().get(i)));
        }
        this.time = balance.getTime();
        this.amountOfEmptyWorkstations = balance.getAmountOfEmptyWorkstations();
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

    public int getMostLoadedWorkstation() {
        return mostLoadedWorkstation;
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

    public void setMostLoadedWorkstation(int mostLoadedWorkstation) {
        this.mostLoadedWorkstation = mostLoadedWorkstation;
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

    public void addWork(int index, int work, double time) {
        Workstation workstation = this.workstationList.get(index);
        if(workstation.getWorkList().isEmpty()) {
            this.amountOfEmptyWorkstations--;
        }
        workstation.addWork(work,time);
        if(workstation.getTime() > this.time) {
            this.time = workstation.getTime();
        }
    }

    @Override
    protected Balance clone() throws CloneNotSupportedException {
        Balance newBalance = (Balance)super.clone();
        newBalance.setWorkstationList(new ArrayList<Workstation>(this.getWorkstationList().size()));
        Collections.copy(newBalance.getWorkstationList(), this.getWorkstationList());
        newBalance.setAmountOfWorkstations(this.amountOfWorkstations);
        return newBalance;
    }

    @Override
    public String toString() {
        String workString = "";
        for(int i = 0; i < workstationList.size(); i++) {
            workString += workstationList.get(i) + " ";
        }
        return "Balance{" +
                "workstationList=" + workString +
                ", type=" + type +
                ", time=" + time +
                ", amountOfEmptyWorkstations=" + amountOfEmptyWorkstations +
                ", mostLoadedWorkstation=" + mostLoadedWorkstation +
                ", amountOfWorkstations=" + amountOfWorkstations +
                ", goalFunction=" + goalFunction +
                ", radius=" + radius +
                '}';
    }


    @Override
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
}
