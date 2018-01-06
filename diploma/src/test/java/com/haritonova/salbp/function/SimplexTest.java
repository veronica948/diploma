package com.haritonova.salbp.function;

import com.haritonova.salbp.utility.Simplex;
import org.junit.Test;

import org.junit.Assert;

import static org.junit.Assert.assertEquals;

public class SimplexTest {

    @Test
    public void simplexTest() {
        boolean quit = false;

        // Example problem:
        // maximize 3x + 5y
        // subject to x +  y = 4 and
        //            x + 3y = 6
        float[][] standardized =  {
                { 3,   2,    1,   10},
                { 2,   3,    3,    15},
                {-1,  -1,    1,   -4},
                {-2,  -3,    -4,   0},
        };

        // row and column do not include
        // right hand side values
        // and objective row
        Simplex simplex = new Simplex(3, 3);

        simplex.fillTable(standardized);

        // print it out
        System.out.println("---Starting set---");
        simplex.print();

        // if table is not optimal re-iterate
        while(!quit){
            Simplex.ERROR err = simplex.compute();

            if(err == Simplex.ERROR.IS_OPTIMAL){
                System.out.println("---optimal solution found---");
                simplex.print();
                quit = true;
            }
            else if(err == Simplex.ERROR.UNBOUNDED){
                System.out.println("---Solution is unbounded---");
                quit = true;
            }
        }
        float [][] table = simplex.getTable();
        assertEquals(20.0, table[3][3],0.1);

    }
    @Test
    public void test2() {
        boolean quit = false;

        // Example problem:
        // maximize 3x + 5y
        // subject to x +  y = 4 and
        //            x + 3y = 6
        float[][] standardized =  {
                { 1,   1,    1,  0,   4},
                { 1,   3,    0,  1,   6},
                {-3,  -5,    0,  0,   0}
        };

        // row and column do not include
        // right hand side values
        // and objective row
        Simplex simplex = new Simplex(2, 4);

        simplex.fillTable(standardized);

        // print it out
        System.out.println("---Starting set---");
        simplex.print();

        // if table is not optimal re-iterate
        while(!quit){
            Simplex.ERROR err = simplex.compute();

            if(err == Simplex.ERROR.IS_OPTIMAL){
                System.out.println("---optimal solution found---");
                simplex.print();
                quit = true;
            }
            else if(err == Simplex.ERROR.UNBOUNDED){
                System.out.println("---Solution is unbounded---");
                quit = true;
            }
        }
        float [][] table = simplex.getTable();
        assertEquals(14.0, table[2][4],0.1);
    }
}
