package com.haritonova.salbp.function;

import com.haritonova.salbp.entity.Balance;
import com.haritonova.salbp.entity.Task;

import java.util.ArrayList;

public class Runner {
    public static void main(String[] args) {
        System.out.println("Task1");
        double [] workTimeList = {3,2,1,4,3,2,4};
        int[][] edges = {{1,4}, {2,3},{4,5},{3,5},{5,6},{5,7}};
        Task task = new Task(30,3,7, workTimeList, edges);
        makeTask(task);

        System.out.println("Task2");
        double [] workTimeList2 = {5,3,1,4,3,2,4};
        Task task1 = new Task(30,3,7, workTimeList2, edges);
        makeTask(task1);

        System.out.println("Task 3");
        double [] workTimeList3 = {2,5,1,4,5,3};
        int[][] edges3 = {{6,2},{2,4},{4,5},{4,1}};
        Task task3 = new Task(30,3,6, workTimeList3, edges3);
        makeTask(task3);
    }

    public static void makeTask(Task task) {
        ArrayList<Balance> balances = task.buildBalances();
        System.out.println("BUILD BALANCES");
        System.out.println("size = " + balances.size());
        System.out.println(balances.get(0));
        ArrayList<Balance> optimalBalances = task.findOptimalBalances();

        System.out.println("OPTIMAL BALANCES");
        System.out.println("size = " + optimalBalances.size());
        for (int i = 0; i < optimalBalances.size(); i++) {
            System.out.println(optimalBalances.get(i));
        }
        System.out.println("Feasible amount = " + task.countFeasibleAmount());

        System.out.println("Find w");
        task.findW();
        System.out.println("Find zero radius");
        task.findZeroRadiusBalances();

        System.out.println("RADIUS OPTIMAL BALANCES");
        System.out.println(optimalBalances.size());
        for (int i = 0; i < optimalBalances.size(); i++) {
            System.out.println(optimalBalances.get(i));
        }
        //estimate radius
        System.out.println("ESTIMATE RADIUS OPTIMAL BALANCES");
        for (Balance balance : optimalBalances) {
            task.estimateRadius(balance);
            System.out.println(balance.getRadius());
        }
    }
}
