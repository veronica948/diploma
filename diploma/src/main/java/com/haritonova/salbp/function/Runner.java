package com.haritonova.salbp.function;

import com.haritonova.salbp.entity.Balance;
import com.haritonova.salbp.entity.Task;

import java.util.ArrayList;

/**
 * Created by Veronica on 1/22/2017.
 */
public class Runner {
    public static void main(String[] args) {
        double [] workTimeList = {3,2,1,4,3,2,4};
        Task task = new Task(30,5,3,7, workTimeList);
        int[][] edges = {{1,4}, {2,3},{4,5},{3,5},{5,6},{5,7}};
        task.formPreviousWork(edges);
        ArrayList<Balance> balances = task.buildBalances();
        System.out.println(balances.size());
        System.out.println(balances.get(0));
        System.out.println(balances.get(244));
        ArrayList<Balance> optimalBalances = task.findOptimalBalances();
        System.out.println(optimalBalances.size());
        System.out.println(optimalBalances.get(0));
        System.out.println(optimalBalances.get(1));
        System.out.println(optimalBalances.get(2));
    }
}
