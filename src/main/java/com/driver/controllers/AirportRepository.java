package com.driver.controllers;

import com.driver.model.Airport;
import com.driver.model.City;
import com.driver.model.Flight;
import com.driver.model.Passenger;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class AirportRepository {
    HashMap<String, Airport> AirportDB = new HashMap<>();
    HashMap<Integer, Flight> FlightDB = new HashMap<>();

    HashMap<Integer, Passenger> PassengerDB = new HashMap<>();
    HashMap<Integer, List<Integer>> FlightToPassengerDB = new HashMap<>();

    public void addPassenger(Passenger passenger) {
        Integer passengerID = passenger.getPassengerId();
        PassengerDB.put(passengerID, passenger);
    }

    public void addAirport(Airport airport) {
        String name = airport.getAirportName();
        AirportDB.put(name, airport);
    }

    public String getLargestAirportName() {

        int max = Integer.MIN_VALUE;
        String name = "";

        for (Airport s : AirportDB.values()) {
            if (s.getNoOfTerminals() > max) {
                max = s.getNoOfTerminals();
                name = s.getAirportName();
            } else if (s.getNoOfTerminals() == max) {
                if (s.getAirportName().compareTo(name) < 0) {
                    name = s.getAirportName();
                }
            }

        }
        return name;
    }

    public double getShortestDurationOfPossibleBetweenTwoCities(City fromCity, City toCity) {
        Double duration = Double.MAX_VALUE;
        for (Flight f : FlightDB.values()) {
            if (f.getFromCity() == fromCity && f.getToCity() == toCity) {
                if (f.getDuration() < duration) {
                    duration = f.getDuration();

                }
            }
        }
        if (duration == Double.MAX_VALUE) {
            return -1;
        }
        return duration;
    }

    public int getNumberOfPeopleOn(Date date, String airportName) {
        Airport airport = AirportDB.get(airportName);
        if (airport == null) {
            return 0;
        }

        City city = airport.getCity();
        int count = 0;

        for (Flight f : FlightDB.values()) {
            if (date.equals(f.getFlightDate())) {
                if (f.getFromCity() == city || f.getToCity() == city) {
                    int flightID = f.getFlightId();
                    count = count + FlightToPassengerDB.get(flightID).size();
                }
            }
        }
        return count;
    }

    public int calculateFlightFare(Integer flightId) {
        int noOfPeopleBooked = FlightToPassengerDB.get(flightId).size();
        return noOfPeopleBooked * 50 + 3000;
    }

    public String bookATicket(Integer flightId, Integer passengerId) {
        if (FlightToPassengerDB.get(flightId) != null && (FlightToPassengerDB.get(flightId).size() < FlightDB.get(flightId).getMaxCapacity())) {
            List<Integer> passengers = FlightToPassengerDB.get(flightId);

            if (passengers.contains(passengerId)) {
                return "FAILURE";
            }
            passengers.add(passengerId);
            FlightToPassengerDB.put(flightId, passengers);
            return "SUCCESS";
        } else if (FlightToPassengerDB.get(flightId) == null) {
            FlightToPassengerDB.put(flightId, new ArrayList<>());
            List<Integer> passengers = FlightToPassengerDB.get(flightId);

            if (passengers.contains(passengerId)) {
                return "FAILURE";

            }

            passengers.add(passengerId);
            FlightToPassengerDB.put(flightId, passengers);
            return "SUCCESS";
        }
        return "FAILURE";

    }

    public String cancelATicket(Integer flightId, Integer passengerId) {
        List<Integer> passengers = FlightToPassengerDB.get(flightId);
        if(passengers == null){
            return "FAILURE";
        }
        if(passengers.contains(passengerId)) {
            FlightToPassengerDB.get(flightId).remove(passengerId);
            PassengerDB.remove(passengerId);
            return "SUCCESS";
        }
        return "FAILURE";
    }

    public int countOfBookingsDoneByPassengerAllCombined(Integer passengerId) {
        int count = 0;
        for(Map.Entry<Integer,List<Integer>> entry: FlightToPassengerDB.entrySet()){

            List<Integer> passengers = entry.getValue();
            for(Integer passenger : passengers){
                if(passenger == passengerId){
                    count++;
                }
            }
        }
        return count;

    }

    public void addFlight(Flight flight) {
        Integer i = flight.getFlightId();
        FlightDB.put(i,flight);

    }

    public String getAirportNameFromFlightId(Integer flightId) {
        if(FlightDB.containsKey(flightId)){
            City city = FlightDB.get(flightId).getFromCity();

            for(Airport airport:AirportDB.values()){
                if(airport.getCity().equals(city)){
                    return airport.getAirportName();
                }
            }
        }
        return null;
    }

    public int calculateRevenueOfAFlight(Integer flightId) {

        int noOfPeopleBooked = FlightToPassengerDB.get(flightId).size();
        int totalRevenue = (25*noOfPeopleBooked*noOfPeopleBooked) + (2975 * noOfPeopleBooked);
        return totalRevenue;
    }
}
