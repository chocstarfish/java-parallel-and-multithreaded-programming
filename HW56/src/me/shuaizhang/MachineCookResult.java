package me.shuaizhang;

/**
 * Created by NicolasZHANG on 10/21/15.
 */
public class MachineCookResult {
    public final Food food;

    public final int orderNum;

    public MachineCookResult(Food food, int orderNum) {
        this.food = food;
        this.orderNum = orderNum;
    }
}
