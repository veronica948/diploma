package com.haritonova.salbp.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Veronica on 1/22/2017.
 */
public class Task {
    private int maxTime;
    private int maxWorkstationAmount;
    private int amountOfMutualWorks;
    private int amountOfWorks;
    private double[] workTimeList;
    private ArrayList<Balance> balanceList;
    private ArrayList<Balance> optimalBalanceList;
    private ArrayList<ArrayList<Integer>> previousWork;

    public Task(int maxTime, int maxWorkstationAmount, int amountOfMutualWorks,
                int amountOfWorks, double[] workTimeList) {
        this.maxTime = maxTime;
        this.maxWorkstationAmount = maxWorkstationAmount;
        this.amountOfMutualWorks = amountOfMutualWorks;
        this.amountOfWorks = amountOfWorks;
        this.workTimeList = workTimeList;
        this.previousWork = new ArrayList<ArrayList<Integer>>(amountOfWorks);
        for(int i = 0; i < amountOfWorks; i++) {
            previousWork.add(i, new ArrayList<Integer>());
        }

    }

    public void formPreviousWork(int[][] edges) {
        System.out.println(edges.length);
        for(int i = 0; i < edges.length; i++) {
            System.out.println(edges[i][1]);
            previousWork.get(edges[i][1] - 1).add(edges[i][0]);
        }
    }

    public ArrayList<Balance> buildBalances() {
        balanceList = new ArrayList<Balance>();
        ArrayList<Balance> mBalanceList = new ArrayList<Balance>();
        ArrayList<Balance> currentList;
         for(int m = 2; m <= maxWorkstationAmount; m++) {
             mBalanceList.clear();
            for(int n = 1; n <= amountOfWorks; n++) {
                ArrayList<Integer> prev = previousWork.get(n-1);
                if(n != 1) {
                    currentList = new ArrayList<Balance>(mBalanceList.size());
                    for(int t = 0; t < mBalanceList.size(); t++) {
                        currentList.add(mBalanceList.get(t));
                    }
                    mBalanceList.clear();
                    for(int j = 0; j < currentList.size(); j++) {
                        int k = findWorkstation(currentList.get(j),prev);
                        for(int l = k; l <= m; l++) {
                            Balance b = new Balance(currentList.get(j));
                            b.addWork(l-1, n, workTimeList[n-1]);
                            if(n == amountOfWorks) {
                                int emptyAmount = b.getAmountOfEmptyWorkstations();
                                if(emptyAmount == 0) {
                                    b.setGoalFunction();
                                    mBalanceList.add(b);
                                }
                            } else {
                                mBalanceList.add(b);
                            }
                        }
                    }
                } else {
                    for(int l = 0; l < m; l++) {
                        Balance b = new Balance(m);
                        b.addWork(l, n, workTimeList[n-1]);
                        mBalanceList.add(b);
                    }
                }
            }
            balanceList.addAll(mBalanceList);
        }
        Collections.sort(balanceList, new Comparator<Balance>() {
            @Override
            public int compare(Balance o1, Balance o2) {
                if(o1.getGoalFunction() > o2.getGoalFunction()) {
                    return 1;
                } else {
                    if(o1.getGoalFunction() < o2.getGoalFunction()) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            }
        });
        return balanceList;
    }

    private int findWorkstation(Balance balance, ArrayList<Integer> prev) {
        if(prev.isEmpty()) {
            return 1;
        } else {
            int r = 0;
            ArrayList<Workstation> workstations = balance.getWorkstationList();
            for(int i = 0; i < workstations.size(); i++) {
                for(int j = 0; j < prev.size(); j++) {
                    if(workstations.get(i).getWorkList().contains(prev.get(j))) {
                        r = i;
                        break;
                    }
                }
            }

            return r + 1;
        }
    }

    public ArrayList<Balance> findOptimalBalances() {
        this.optimalBalanceList = new ArrayList<Balance>();
        double goalFunction = this.balanceList.get(0).getGoalFunction();
        for(int i = 0; i < this.balanceList.size(); i++) {
            Balance balance = this.balanceList.get(i);
            if(balance.getGoalFunction() > goalFunction) {
                break;
            }
            this.optimalBalanceList.add(balance);
        }
        return this.getOptimalBalanceList();
    }

    public void solveTask() {

    }

    public int getMaxTime() {
        return maxTime;
    }

    public int getMaxWorkstationAmount() {
        return maxWorkstationAmount;
    }

    public int getAmountOfMutualWorks() {
        return amountOfMutualWorks;
    }

    public int getAmountOfWorks() {
        return amountOfWorks;
    }

    public double[] getWorkTimeList() {
        return workTimeList;
    }

    public ArrayList<Balance> getBalanceList() {
        return balanceList;
    }

    public ArrayList<Balance> getOptimalBalanceList() {
        return optimalBalanceList;
    }

    public ArrayList<ArrayList<Integer>> getPreviousWork() {
        return previousWork;
    }
}
