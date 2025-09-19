package com.example.olioohjelmointiharjoitusty.ShowData;

public class PopulationData {
    private int population;
    private double populationChangePercent;

    public PopulationData(int population, double populationChangePercent) {
        this.population = population;
        this.populationChangePercent = populationChangePercent;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public double getPopulationChangePercent() {
        return populationChangePercent;
    }

    public void setPopulationChangePercent(double populationChangePercent) {
        this.populationChangePercent = populationChangePercent;
    }
}