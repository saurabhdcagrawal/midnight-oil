package main.java.beans;

public class Country {
    int id;
    String countryName;
    long population;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public long getPopulation() {
        return population;
    }

    public void setPopulation(long population) {
        this.population = population;
    }

    public double getMilitaryExpenditure() {
        return militaryExpenditure;
    }

    public void setMilitaryExpenditure(double militaryExpenditure) {
        this.militaryExpenditure = militaryExpenditure;
    }

    double militaryExpenditure;

    public Country(String countryName, long population, double militaryExpenditure) {
        this.countryName = countryName;
        this.population = population;
        this.militaryExpenditure = militaryExpenditure;
    }


}
