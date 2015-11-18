package me.shuai;

/**
 * Created by Shuai Zhang on 10/31/15.
 */
public class Test {
    public static void main(String[] args) {
        String a = "123456";
        char[] charArray = a.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            char c = charArray[i];
            System.out.println(c);
        }
    }
}
