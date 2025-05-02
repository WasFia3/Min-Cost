package com.example.dynamic;


public class City {
    private String name;
    private int stage;
    private int hotelCost;
    private int[] petrolCost; // Array of petrol costs from this city to other cities

    public City(String name, int stage, int hotelCost, int[] petrolCost) {
        this.name = name;
        this.stage = stage;
        this.hotelCost = hotelCost;
        this.petrolCost = petrolCost;
    }

    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public int getStage() {
        return stage;
    }


    public void setStage(int stage) {
        this.stage = stage;
    }


    public int getHotelCost() {
        return hotelCost;
    }

    public void setHotelCost(int hotelCost) {
        this.hotelCost = hotelCost;
    }


    public int[] getPetrolCost() {
        return petrolCost;
    }


    public void setPetrolCost(int[] petrolCost) {
        this.petrolCost = petrolCost;
    }

}
