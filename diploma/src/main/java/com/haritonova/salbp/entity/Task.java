package com.haritonova.salbp.entity;

import java.util.*;

public class Task {
    private int maxTime;
    private int amountOfManualWorks;
    private int amountOfWorks;
    private double[] workTimeList;
    private ArrayList<Balance> balanceList;
    private ArrayList<Balance> optimalBalanceList;
    private ArrayList<ArrayList<Integer>> previousWork;
    private int amountOfFeasibleBalances;
    int[][] edges;

    public Task(int maxTime,  int amountOfManualWorks,
                int amountOfWorks, double[] workTimeList, int[][] edges) {
        this.maxTime = maxTime;
        this.amountOfManualWorks = amountOfManualWorks;
        this.amountOfWorks = amountOfWorks;
        this.workTimeList = workTimeList;
        this.previousWork = new ArrayList<ArrayList<Integer>>(amountOfWorks);
        for(int i = 0; i < amountOfWorks; i++) {
            previousWork.add(i, new ArrayList<Integer>());
        }
        this.edges = edges;
        this.formPreviousWork(edges);
    }

    public void formPreviousWork(int[][] edges) {
        for(int i = 0; i < edges.length; i++) {
            int prev = edges[i][0];
            previousWork.get(edges[i][1] - 1).add(prev);
        }

        for(int i = 0; i < previousWork.size(); i++) {
            ArrayList<Integer> currentPrevWork = previousWork.get(i);
            for(int j = 0; j < currentPrevWork.size(); j++) {
                int p = currentPrevWork.get(j);
                ArrayList<Integer> prevPrev = previousWork.get(p-1);
                previousWork.get(i).addAll(prevPrev);
            }
        }
    }

    public ArrayList<Balance> buildBalances() {
        balanceList = new ArrayList<Balance>();
        ArrayList<Balance> mBalanceList = new ArrayList<Balance>();
        ArrayList<Balance> currentList;
         for(int m = 2; m <= amountOfWorks; m++) {
            mBalanceList.clear();
            for(int n = 1; n <= amountOfWorks; n++) {
                ArrayList<Integer> prev = previousWork.get(n-1);
                boolean isManual = false;
                if(n <= amountOfManualWorks) {
                    isManual = true;
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
                            b.addWork(l-1, n, workTimeList[n-1], isManual);
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
                        b.addWork(l, n, workTimeList[n-1], isManual);
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
            balance.findManualMostLoaded();
        }
    }

    public void findZeroRadiusBalances() {
        for(int i = 0; i < optimalBalanceList.size(); i++) {
            Balance balance1 = optimalBalanceList.get(i);
            ArrayList<ArrayList<Integer>> balance1W = balance1.getW();
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
                            balance1.setRadius(0);
                            break;
                        }
                    }
                }

                if(balance1.getAmountOfWorkstations() != balance2.getAmountOfWorkstations()) {
                    if(!balance2W.contains(null)) {
                        balance1.setRadius(0);
                        break;
                    }
                }
                if(balance1.getAmountOfWorkstations() > balance2.getAmountOfWorkstations()) {
                    for(int t = 0; t < balance1W.size(); t++) {
                        if(balance1W.get(t) != null) {
                            balance1.setRadius(0);
                            break;
                        }
                    }
                }
                if(balance1.getRadius() == 0) {
                    break;
                }
            }
        }
    }

    public int getMaxTime() {
        return maxTime;
    }

    public int getAmountOfManualWorks() {
        return amountOfManualWorks;
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

    /*
     * Находится e-окрестность, при попадании
     * в которую баланс перестает быть оптимальный
     * в сранении с данным балансом
     */

    //m0 = m
    public double calculateSameWorkstationNumberRadius(Balance balance1, Balance balance2) {
        double current;
        double currentMaxWorkstation;
        double currentMinBalance = Double.POSITIVE_INFINITY;
        boolean isConsidered;
        for(Workstation workstation1 : balance1.getWorkstationList()) {

            currentMaxWorkstation = Double.NEGATIVE_INFINITY;
            isConsidered = false;
            for(Workstation workstation2 : balance2.getWorkstationList()) {
                if(workstation2.getTime() <= workstation1.getTime()) {
                    continue;
                }
                ArrayList<Integer> works1 = new ArrayList<Integer>(workstation1.getManualWorkList().size());
                for(Integer work : workstation1.getManualWorkList()) {
                    works1.add(work);
                }
                ArrayList<Integer> works2 = new ArrayList<Integer>(workstation2.getManualWorkList().size());
                for(Integer work : workstation2.getManualWorkList()) {
                    works2.add(work);
                }
                works1.removeAll(workstation2.getManualWorkList());
                works2.removeAll(workstation1.getManualWorkList());
                ArrayList<Integer> difference = new ArrayList<Integer>();
                difference.addAll(works1);
                difference.addAll(works2);
                ArrayList<Double> sortedManualWorks = new ArrayList<Double>(works2.size());
                for(Integer work : works2) {
                    sortedManualWorks.add(workTimeList[work - 1]);
                }
                Collections.sort(sortedManualWorks);
                if (difference.size() != 0) {
                    isConsidered = true;
                    double max = (workstation2.getTime() - workstation1.getTime()) / (difference.size());
                    double sum = 0;
                    double counter = 0;
                    for(Double entry : sortedManualWorks) {
                        sum += entry;
                        counter++;
                        current = (workstation2.getTime() - workstation1.getTime() - sum) / (difference.size() - counter);
                        if(current > max) {
                            max = current;
                        } else {
                            break;
                        }
                    }
                    if (currentMaxWorkstation < max) {
                        currentMaxWorkstation = max;
                    }
                }
            }
            if(currentMaxWorkstation < currentMinBalance && isConsidered) {
                currentMinBalance = currentMaxWorkstation;
            }
        }
        return currentMinBalance;
    }

    //m0 > m
    public double calculateMoreWorkstationNumberRadius(Balance balance1, Balance balance2) {
        int m1 = balance1.getAmountOfWorkstations();
        int m2 = balance2.getAmountOfWorkstations();
        double current;
        double currentMaxWorkstation;
        double currentMinBalance = Double.POSITIVE_INFINITY;
        boolean isConsidered;
        for(Workstation workstation1 : balance1.getWorkstationList()) {
            currentMaxWorkstation = Double.NEGATIVE_INFINITY;
            isConsidered = false;
            for(Workstation workstation2 : balance2.getWorkstationList()) {
                if(workstation2.getTime()*m2 <= workstation1.getTime()*m1) {
                    continue;
                }
                ArrayList<Integer> works1 = new ArrayList<Integer>(workstation1.getManualWorkList().size());
                ArrayList<Integer> intersection = new ArrayList<Integer>(workstation1.getManualWorkList().size());
                for(Integer work : workstation1.getManualWorkList()) {
                    works1.add(work);
                    intersection.add(work);
                }
                ArrayList<Integer> works2 = new ArrayList<Integer>(workstation2.getManualWorkList().size());
                for(Integer work : workstation2.getManualWorkList()) {
                    works2.add(work);
                }
                works1.removeAll(workstation2.getManualWorkList());
                works2.removeAll(workstation1.getManualWorkList());
                intersection.removeAll(works1);
                ArrayList<Integer> difference = new ArrayList<Integer>();
                difference.addAll(works1);
                difference.addAll(works2);
                ArrayList<Double> sortedManualWorks = new ArrayList<Double>(works2.size());
                for(Integer work : works2) {
                    sortedManualWorks.add(workTimeList[work - 1]);
                }
                Collections.sort(sortedManualWorks);
                if (difference.size() != 0 || intersection.size() != 0) {
                    isConsidered = true;
                    double max = (workstation2.getTime() * m2 - workstation1.getTime() * m1) /
                            (works1.size()*m1 + (m1 - m2) * intersection.size() + (works2.size()) * m2);
                    double sum = 0;
                    double counter = 0;
                    for(Double entry : sortedManualWorks) {
                        sum += entry;
                        counter++;
                        current = (workstation2.getTime() * m2 - workstation1.getTime() * m1 - sum * m2) /
                                (works1.size()*m1 + (m1 - m2) * intersection.size() + (works2.size() - counter) * m2);
                        if(current > max) {
                            max = current;
                        } else {
                            break;
                        }

                    }
                    if (currentMaxWorkstation < max) {
                        currentMaxWorkstation = max;
                    }
                } else {
                    isConsidered = false;
                    break;
                }
            }
            if(currentMaxWorkstation < currentMinBalance && isConsidered) {
                currentMinBalance = currentMaxWorkstation;
            }
        }
        return currentMinBalance;
    }

    //m0 < m
    public double estimateLessWorkstationNumberRadius(Balance balance1, Balance balance2) {
        int m1 = balance1.getAmountOfWorkstations();
        int m2 = balance2.getAmountOfWorkstations();

        double current;
        double currentMaxWorkstation;
        double currentMinBalance = Double.POSITIVE_INFINITY;
        boolean isConsidered;

        double currentMaxWorkstation2;
        double currentMinBalance2 = Double.POSITIVE_INFINITY;
        boolean isConsidered2;

        boolean skip = false;
        boolean skip2 = false;
        for(Workstation workstation1 : balance1.getWorkstationList()) {
            currentMaxWorkstation = Double.NEGATIVE_INFINITY;
            isConsidered = false;

            currentMaxWorkstation2 = Double.NEGATIVE_INFINITY;
            isConsidered2 = false;
            skip2 = false;

            skip = false;

            int[] intersectionSizes = new int[balance2.getWorkstationList().size()];
            ArrayList<ArrayList<Integer>> differences = new ArrayList<ArrayList<Integer>>(balance2.getWorkstationList().size());
            int i = 0;
            for(Workstation workstation2 : balance2.getWorkstationList()) {
                if(workstation2.getTime()*m2 <= workstation1.getTime()*m1) {
                    continue;
                }
                ArrayList<Integer> works1 = new ArrayList<Integer>(workstation1.getManualWorkList().size());
                ArrayList<Integer> intersection = new ArrayList<Integer>(workstation1.getManualWorkList().size());
                for(Integer work : workstation1.getManualWorkList()) {
                    works1.add(work);
                    intersection.add(work);
                }
                ArrayList<Integer> works2 = new ArrayList<Integer>(workstation2.getManualWorkList().size());
                for(Integer work : workstation2.getManualWorkList()) {
                    works2.add(work);
                }
                works1.removeAll(workstation2.getManualWorkList());
                works2.removeAll(workstation1.getManualWorkList());
                intersection.removeAll(works1);
                ArrayList<Integer> difference = new ArrayList<Integer>();
                difference.addAll(works1);
                difference.addAll(works2);
                ArrayList<Double> sortedManualWorks = new ArrayList<Double>(works2.size());
                for(Integer work : works2) {
                    sortedManualWorks.add(workTimeList[work - 1]);
                }
                intersectionSizes[i] = intersection.size();
                differences.add(new ArrayList<Integer>());
                differences.get(i).addAll(works2);
                i++;
                Collections.sort(sortedManualWorks);
                if (works2.size() != 0) {
                    isConsidered = true;
                    double max = (workstation2.getTime() * m2 - workstation1.getTime() * m1) /
                            ( (works2.size()) * m2);
                    double sum = 0;
                    double counter = 0;
                    for(Double entry : sortedManualWorks) {
                        sum += entry;
                        counter++;
                        current = (workstation2.getTime() * m2 - workstation1.getTime() * m1 - sum * m2) /
                                ((works2.size() - counter) * m2);
                        if(current > max) {
                            max = current;
                        } else {
                            break;
                        }
                    }
                    if(workstation2.getTime() * m2 - sum * m2 > workstation1.getTime() * m1 ) {
                        isConsidered = false;
                        skip = true;
                    }
                    else {
                        if(currentMaxWorkstation < max) {
                            currentMaxWorkstation = max;
                        }
                    }
                } else {
                    isConsidered = false;
                    skip = true;
                }
                //estimation 2

                if (works2.size() + works1.size() != 0 &&
                        works1.size()*m1 + works2.size()*m2 + (m1-m2)*intersection.size() > 0) {
                    isConsidered2 = true;
                    double max = (workstation2.getTime() * m2 - workstation1.getTime() * m1) /
                            ( (works1.size()) * m1 + intersection.size()*(m1-m2) + (works2.size()) * m2);
                    double sum = 0;
                    double counter = 0;
                    for(Double entry : sortedManualWorks) {
                        sum += entry;
                        counter++;
                        current = (workstation2.getTime() * m2 - workstation1.getTime() * m1 - sum * m2) /
                                (works2.size() * m1 + intersection.size()*(m1-m2)+ (works2.size() - counter) * m2);

                        if(current > max) {
                            max = current;
                        } else {
                            break;
                        }
                    }
                    if (currentMaxWorkstation2 < max) {
                        currentMaxWorkstation2 = max;
                    }
                } else {
                    isConsidered2 = false;
                    skip2 = true;
                }
            }
            if(currentMaxWorkstation == Double.POSITIVE_INFINITY) {
                skip = true;
            }
            if(!skip && isConsidered) {
                System.out.println("estimation 1 works for station " + workstation1);
                System.out.println("and balance " + balance2);
                System.out.println(" = " + currentMaxWorkstation);
            }
            if(currentMaxWorkstation < currentMinBalance && isConsidered && !skip) {
                currentMinBalance = currentMaxWorkstation;
            }

            if(currentMaxWorkstation2 == Double.POSITIVE_INFINITY) {
                skip2 = true;
            }
            //check
            if(!skip2) {
                double newTime1 = m1 * workstation1.getTime() + currentMaxWorkstation2 * m1 * workstation1.getManualWorkList().size();
                int j = 0;
                for (Workstation workstation22 : balance2.getWorkstationList()) {
                    double newTime2 = m2 * workstation22.getTime() + m2 * intersectionSizes[j];
                    for (Integer w : differences.get(j)) {
                        if (workTimeList[w - 1] < currentMaxWorkstation2) {
                            newTime2 -= m2 * workTimeList[w - 1];
                        } else {
                            newTime2 -= m2 * currentMaxWorkstation2;
                        }
                    }
                    if (newTime2 > newTime1) {
                        skip2 = true;
                        break;
                    }

                }
                j++;
            }

            if(!skip2 && isConsidered2) {
                System.out.println("estimation 2 works for station " + workstation1);
                System.out.println("and balance " + balance2);
                System.out.println(" = " + currentMaxWorkstation2);
            }
            if(currentMaxWorkstation2 < currentMinBalance2 && isConsidered2 && !skip2) {
                currentMinBalance2 = currentMaxWorkstation2;
            }
        }
        if(currentMinBalance > currentMinBalance2) {
            return currentMinBalance2;
        } else {
            return currentMinBalance;
        }
    }

    //m0 < m
    public double lowerBoundRadius(Balance balance1, Balance balance2) {
        int m1 = balance1.getAmountOfWorkstations();
        int m2 = balance2.getAmountOfWorkstations();

        double current;
        double currentMaxWorkstation;
        double currentMinBalance = Double.POSITIVE_INFINITY;
        boolean isConsidered;

        boolean skip = false;
        for(Workstation workstation1 : balance1.getWorkstationList()) {
            currentMaxWorkstation = Double.NEGATIVE_INFINITY;
            isConsidered = false;
            skip = false;
            for(Workstation workstation2 : balance2.getWorkstationList()) {
                if(workstation2.getTime()*m2 <= workstation1.getTime()*m1) {
                    continue;
                }
                ArrayList<Integer> works1 = new ArrayList<Integer>(workstation1.getManualWorkList().size());
                ArrayList<Integer> intersection = new ArrayList<Integer>(workstation1.getManualWorkList().size());
                for(Integer work : workstation1.getManualWorkList()) {
                    works1.add(work);
                    intersection.add(work);
                }
                ArrayList<Integer> works2 = new ArrayList<Integer>(workstation2.getManualWorkList().size());
                for(Integer work : workstation2.getManualWorkList()) {
                    works2.add(work);
                }
                works1.removeAll(workstation2.getManualWorkList()); //difference 1
                works2.removeAll(workstation1.getManualWorkList()); //difference 2
                intersection.removeAll(works1);
                ArrayList<Double> sortedManualWorks = new ArrayList<Double>(workstation2.getManualWorkList().size());
                for(Integer work : workstation2.getManualWorkList()) {
                    sortedManualWorks.add(workTimeList[work - 1]);
                }
                Collections.sort(sortedManualWorks);
                if (sortedManualWorks.size() + works1.size() != 0) {
                    isConsidered = true;
                    double max = (workstation2.getTime() * m2 - workstation1.getTime() * m1) /
                            ( (works1.size()) * m1 + intersection.size()*(m2-m1) + (works2.size()) * m2);
                    double sum1 = 0;
                    double sum2 = 0;
                    double counter1 = 0;
                    double counter2 = 0;
                    for(Double entry : sortedManualWorks) {
                        if(intersection.contains(entry)) {
                            sum1 += entry;
                            counter1++;
                        } else {
                            sum2 += entry;
                            counter2++;
                        }
                        current = (workstation2.getTime() * m2 - workstation1.getTime() * m1 - sum2 * m2 - sum1 * (m2-m1)) /
                                (works2.size() * m1 + (intersection.size() - counter1)*(m2-m1)+ (works2.size() - counter2) * m2);

                        if(current > max) {
                            max = current;
                        } else {
                            break;
                        }
                    }
                    if (currentMaxWorkstation < max) {
                        currentMaxWorkstation = max;
                    }
                } else {
                    isConsidered = false;
                    skip = true;
                }
            }
            if(currentMaxWorkstation == Double.POSITIVE_INFINITY) {
                skip = true;
            }
            if(!skip && isConsidered) {
                System.out.println("lower bound works for station " + workstation1);
                System.out.println("and balance " + balance2);
                System.out.println(" = " + currentMaxWorkstation);
            }
            if(currentMaxWorkstation < currentMinBalance && isConsidered && !skip) {
                currentMinBalance = currentMaxWorkstation;
            }
        }
        return currentMinBalance;
    }

    public double estimateRadius(Balance balance) {
        if(balance.getRadius() == 0  || balance.getRadius() == Double.POSITIVE_INFINITY) {
            return balance.getRadius();
        }
        double radius1 = Double.POSITIVE_INFINITY; // m0 = m
        double radius2  = Double.POSITIVE_INFINITY; //m0 > m
        double radius3  = Double.POSITIVE_INFINITY; // m0 < m
        double r;
        double lowerBound3 = Double.POSITIVE_INFINITY;
        double l;
        int workstationAmount = balance.getAmountOfWorkstations();
        boolean sameWorkstationNumberConsidered = false;
        boolean moreWorkstationNumberConsidered = false;
        boolean lessWorkstationNumberConsidered = false;

        for(Balance currentBalance : balanceList) {
            if(currentBalance.getType() == BalanceType.OPTIMAL) {
                continue;
            }
            if (currentBalance.getAmountOfWorkstations() == workstationAmount) {
                sameWorkstationNumberConsidered = true;
                r = calculateSameWorkstationNumberRadius(balance, currentBalance);
                if (r < radius1) {
                    radius1 = r;
                }
            } else {
                if (currentBalance.getAmountOfWorkstations() < workstationAmount) {
                    moreWorkstationNumberConsidered = true;
                    r = calculateMoreWorkstationNumberRadius(balance, currentBalance);
                    if (r < radius2) {
                        radius2 = r;
                    }
                } else {
                    lessWorkstationNumberConsidered = true;
                    r = estimateLessWorkstationNumberRadius(balance, currentBalance);
                    l = lowerBoundRadius(balance, currentBalance);
                    if (r < radius3) {
                        radius3 = r;
                    }
                    if (l < lowerBound3) {
                        lowerBound3 = l;
                    }
                }
            }
        }
        ArrayList<Double> list = new ArrayList<Double>(3);
        if(sameWorkstationNumberConsidered) {
            System.out.println("estimation1 = " + radius1);
            list.add(radius1);
        }
        if(moreWorkstationNumberConsidered) {
            System.out.println("estimation2 = " + radius2);
            list.add(radius2);
        }
        if(lessWorkstationNumberConsidered) {
            System.out.println("estimation3 = " + radius3);
            list.add(radius3);
            System.out.println("lower bound3 = " + lowerBound3);
        }
        double radius = Collections.min(list);
        System.out.println("estimation = " + radius);
        if(!lessWorkstationNumberConsidered) {
            balance.setRadius(radius);
        }
        return radius;
    }
}
