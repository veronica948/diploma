package com.haritonova.salbp.function;

import com.haritonova.salbp.entity.Balance;
import com.haritonova.salbp.entity.Task;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Created by Veronica on 1/24/2017.
 */

public class TaskTest {
    @Test
    public void testBalances() {
        double [] workTimeList = {3,2,1};
        Task task = new Task(30,3,2,3, workTimeList);
        int[][] edges = {{1,3}};
        task.formPreviousWork(edges);
        ArrayList<Balance> balances = task.buildBalances();
        System.out.println(balances.size());
        System.out.println(balances.get(0));
        assertEquals(7, balances.size());
    }
    @Test
    public void testOptimalBalances() {
        double [] workTimeList = {3,2,1};
        Task task = new Task(30,3,2,3, workTimeList);
        int[][] edges = {{1,3}};
        task.formPreviousWork(edges);
        task.buildBalances();
        ArrayList<Balance> optimalBalances = task.findOptimalBalances();
        assertEquals(1, optimalBalances.size());
    }
}
