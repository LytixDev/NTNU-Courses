package com.example.ballekreft.model;


import jakarta.persistence.*;

@Entity(name = "Equation")
@Table
public class Equation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "equation_id", unique = true, nullable = false)
    private Long id;

    @Column(name = "equation", nullable = false)
    private String equation;

    @Column(name = "result", nullable = false)
    private double result;

    public Equation(Long id, String equation, Double result) {
        this.id = id;
        this.equation = equation;
        this.result = result;
    }

    public Equation(Expr expr) {
        addExpr(expr);
    }

    public Equation() {

    }

    public void addExpr(Expr expr) {
        this.equation = expr.toString();
        this.result = Integer.parseInt(expr.eval());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEquation() {
        return equation;
    }

    public void setEquation(String equation) {
        this.equation = equation;
    }

    public Double getResult() {
        return result;
    }

    public void setResult(Double result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "Equation{" +
                "id=" + id +
                ", equation='" + equation + '\'' +
                ", result=" + result +
                '}';
    }
}
