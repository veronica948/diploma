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
    private ArrayList<ArrayList<Integer>> pastWork;
    private int amountOfFeasibleBalances;
    int[][] edges;

    public Task(int maxTime, int maxWorkstationAmount, int amountOfMutualWorks,
                int amountOfWorks, double[] workTimeList, int[][] edges) {
        this.maxTime = maxTime;
        this.maxWorkstationAmount = maxWorkstationAmount;
        this.amountOfMutualWorks = amountOfMutualWorks;
        this.amountOfWorks = amountOfWorks;
        this.workTimeList = workTimeList;
        this.previousWork = new ArrayList<ArrayList<Integer>>(amountOfWorks);
        for(int i = 0; i < amountOfWorks; i++) {
            previousWork.add(i, new ArrayList<Integer>());
        }

        this.pastWork = new ArrayList<ArrayList<Integer>>(amountOfWorks);
        for(int i = 0; i < amountOfWorks; i++) {
            pastWork.add(i, new ArrayList<Integer>());
        }
        this.edges = edges;
        this.formPreviousWork(edges);
        formPastWork(edges);
    }

    public void formPreviousWork(int[][] edges) {
        System.out.println("edges.length" + edges.length);
        for(int i = 0; i < edges.length; i++) {
            int prev = edges[i][0];
            System.out.println(edges[i][1]);
            previousWork.get(edges[i][1] - 1).add(prev);
            //previousWork.get(edges[i][1] - 1).addAll(previousWork.get(prev - 1));
        }

        for(int i = 0; i < previousWork.size(); i++) {
            ArrayList<Integer> currentPrevWork = previousWork.get(i);
            for(int j = 0; j < currentPrevWork.size(); j++) {
                int p = currentPrevWork.get(j);
                ArrayList<Integer> prevPrev = previousWork.get(p-1);
                previousWork.get(i).addAll(prevPrev);
            }
        }
        System.out.println("Previous works = " + previousWork);

    }

    public void formPastWork(int[][] edges) {
        System.out.println("edges.length" + edges.length);
        for(int i = 0; i < edges.length; i++) {
            int past = edges[i][1];
            System.out.println(edges[i][0]);
            pastWork.get(edges[i][0] - 1).add(past);
            pastWork.get(edges[i][0] - 1).addAll(pastWork.get(past - 1));
        }
        System.out.println("Past works = " + pastWork);
    }

    public ArrayList<Balance> buildBalances() {
        balanceList = new ArrayList<Balance>();
        ArrayList<Balance> mBalanceList = new ArrayList<Balance>();
        ArrayList<Balance> currentList;
         for(int m = 2; m <= maxWorkstationAmount; m++) {
            mBalanceList.clear();
            for(int n = 1; n <= amountOfWorks; n++) {
                ArrayList<Integer> prev = previousWork.get(n-1);
                boolean isMutual = false;
                if(n <= amountOfMutualWorks) {
                    isMutual = true;
                }
                if(n != 1) {
                    currentList = new ArrayList<Balance>(mBalanceList.size());
                    for(int t = 0; t < mBalanceList.size(); t++) {
                        currentList.add(mBalanceList.get(t));
                    }
                    mBalanceList.clear();
                    for(int j = 0; j < currentList.size(); j++) {
                        int[] k = findWorkstation(currentList.get(j),prev, n, m);
                        for(int l = k[0]; l <= k[1]; l++) {
                            Balance b = new Balance(currentList.get(j));
                            b.addWork(l-1, n, workTimeList[n-1], isMutual);
                            if(n == amountOfWorks) {
                                int emptyAmount = b.getAmountOfEmptyWorkstations();
                                if(emptyAmount == 0) {
                                    b.setGoalFunction();
                                    if(b.getGoalFunction() > maxTime) {
                                        b.setType(BalanceType.QUASIFEASIBLE);
                                    } else {
                                        b.setType(BalanceType.FEASIBLE);
                                    }
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
                        b.addWork(l, n, workTimeList[n-1], isMutual);
                        mBalanceList.add(b);
                    }
                }
            }
            balanceList.addAll(mBalanceList);
        }
        Collections.sort(balanceList, new Comparator<Balance>() {

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

    private int[] findWorkstation(Balance balance, ArrayList<Integer> prev, int n, int m) {
        ArrayList<Workstation> workstations = balance.getWorkstationList();
        int r = 0;
            for(int i = 0; i < workstations.size(); i++) {
                for(int j = 0; j < prev.size(); j++) {
                    if(workstations.get(i).getWorkList().contains(prev.get(j))) {
                        r = i;
                        break;
                    }
                }
            }

        int t = m - 1;
        for(int i = m - 1; i >= r; i--) {
            ArrayList<Integer> workList = workstations.get(i).getWorkList();
            for(int j = 0; j < workList.size(); j++) {
                ArrayList<Integer> pp = previousWork.get(workList.get(j) - 1);
                if(pp.contains(n)) {
                    t = i;
                    break;
                }
            }
        }
        return new int[]{r + 1, t + 1};
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
            balance.setType(BalanceType.OPTIMAL);
        }
        return this.getOptimalBalanceList();
    }

    public int countFeasibleAmount() {
        int quasifeasibleAmount = 0;
        for(int i = this.balanceList.size() - 1; i >= 0 ; i--) {
            Balance balance = this.balanceList.get(i);
            if(balance.getGoalFunction() > maxTime) {
                quasifeasibleAmount++;
            } else {
                break;
            }
        }
        this.amountOfFeasibleBalances = this.balanceList.size() - quasifeasibleAmount;
        return amountOfFeasibleBalances;
    }

    public void findW() {
        for(int i = 0; i < optimalBalanceList.size(); i++) {
            Balance balance = optimalBalanceList.get(i);
            balance.findMutualMostLoaded();
        }
    }

    public void findZeroRadiusBalances() {
        for(int i = 0; i < optimalBalanceList.size(); i++) {
            Balance balance1 = optimalBalanceList.get(i);
            ArrayList<ArrayList<Integer>> balance1W = balance1.getW();
            System.out.println(balance1);
            for(int j = 0; j < optimalBalanceList.size(); j++) {
                if(j == i) {
                    continue;
                }

                Balance balance2 = optimalBalanceList.get(j);
                ArrayList<ArrayList<Integer>> balance2W = balance2.getW();
                if(balance1W.size() > balance2W.size()) {
                    balance1.setRadius(0);
                    break;
                } else {
                    for(int k = 0; k < balance1W.size(); k++) {
                        if(!balance2W.contains(balance1W.get(k))) {
                            System.out.println(balance2W + "not " + balance1W.get(k));
                            System.out.println("not contains");
                            balance1.setRadius(0);
                            break;
                        } else {
                            System.out.println(balance2W + " contains  " + balance1W.get(k));
                            System.out.println("contains");
                        }


                    }
                }
            }
        }
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

    public int getAmountOfFeasibleBalances() {
        return amountOfFeasibleBalances;
    }

    public void setAmountOfFeasibleBalances(int amountOfFeasibleBalances) {
        this.amountOfFeasibleBalances = amountOfFeasibleBalances;
    }

}
