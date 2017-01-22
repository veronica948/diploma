package com.haritonova.salbp.function;

import com.haritonova.salbp.entity.Task;

/**
 * Created by Veronica on 1/22/2017.
 */
public class Runner {
    public static void main(String[] args) {
        double [] workTimeList = {3,2,1,4,3,2,4};
        Task task = new Task(30,5,3,7, workTimeList);
        int[][] edges = {{1,4}, {2,3},{4,5},{3,5},{5,6},{5,7}};
        task.formPreviousWork(edges);
        System.out.println(task.buildBalances().size());
        System.out.println(task.buildBalances().get(0).getWorkstationList());
        System.out.println(task.buildBalances().get(1).getWorkstationList());
        System.out.println(task.buildBalances().get(2).getWorkstationList());
        System.out.println(task.buildBalances().get(155).getWorkstationList());
        System.out.println(task.buildBalances().get(155));
    }
}
