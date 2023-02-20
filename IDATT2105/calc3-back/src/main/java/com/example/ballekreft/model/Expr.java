package com.example.ballekreft.model;

public class Expr {
    private final int left;
    private final char operator;
    private final int right;

    public Expr(int left, char operator, int right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public String eval() {
        int res;
        try {
            switch (operator) {
                case '+':
                    res = left + right;
                    break;
                case '-':
                    res = left - right;
                    break;
                case '*':
                    res = left * right;
                    break;
                case '/':
                    res = left / right;
                    break;
                default:
                    return "error: Operator not supported";
            }
        } catch (Exception e) {
            return e.getMessage();
        }
        return Integer.toString(res);
    }

    @Override
    public String toString() {
        return Integer.toString(this.left) + this.operator + this.right;
    }
}
