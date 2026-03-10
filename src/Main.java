/**
 * Main
 *
 * Hotel Booking Application
 * Demonstrates room modeling and centralized inventory management.
 *
 * UC1: Application entry and welcome message
 * UC2: Room abstraction and room types
 * UC3: Centralized inventory using HashMap
 *
 * @author Dhruv
 * @version 1.0
 */

import java.util.HashMap;
import java.util.Map;

public class Main {

    // -------------------------
    // UC2: Abstract Room Model
    // -------------------------

    static abstract class Room {

        protected int beds;
        protected double size;
        protected double price;

        public Room(int beds, double size, double price) {
            this.beds = beds;
            this.size = size;
            this.price = price;
        }

        public void displayDetails() {
            System.out.println("Beds: " + beds);
            System.out.println("Size: " + size + " sqm");
            System.out.println("Price: $" + price + " per night");
        }

        public abstract String getRoomType();
    }

    static class SingleRoom extends Room {

        public SingleRoom() {
            super(1, 20, 100);
        }

        public String getRoomType() {
            return "Single Room";
        }
    }

    static class DoubleRoom extends Room {

        public DoubleRoom() {
            super(2, 30, 180);
        }

        public String getRoomType() {
            return "Double Room";
        }
    }

    static class SuiteRoom extends Room {

        public SuiteRoom() {
            super(3, 50, 350);
        }

        public String getRoomType() {
            return "Suite Room";
        }
    }

    // -------------------------
    // UC3: Centralized Inventory
    // -------------------------

    static class RoomInventory {

        private HashMap<String, Integer> inventory;

        // Constructor initializes availability
        public RoomInventory() {
            inventory = new HashMap<>();

            inventory.put("Single Room", 5);
            inventory.put("Double Room", 3);
            inventory.put("Suite Room", 2);
        }

        // Get availability of a room type
        public int getAvailability(String roomType) {
            return inventory.getOrDefault(roomType, 0);
        }

        // Update availability
        public void updateAvailability(String roomType, int newCount) {
            inventory.put(roomType, newCount);
        }

        // Display full inventory
        public void displayInventory() {
            System.out.println("\nCurrent Room Inventory:");

            for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
                System.out.println(entry.getKey() + " → Available: " + entry.getValue());
            }
        }
    }

    // -------------------------
    // Main Method
    // -------------------------

    public static void main(String[] args) {

        // UC1: Welcome Message
        System.out.println("Welcome to the Hotel Booking System");
        System.out.println("Application: Hotel Booking System");
        System.out.println("Version: v1.0");
        System.out.println("Application started successfully.\n");

        // UC2: Room objects
        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        System.out.println("Room Types:\n");

        System.out.println(single.getRoomType());
        single.displayDetails();
        System.out.println();

        System.out.println(doubleRoom.getRoomType());
        doubleRoom.displayDetails();
        System.out.println();

        System.out.println(suite.getRoomType());
        suite.displayDetails();

        // UC3: Centralized inventory
        RoomInventory inventory = new RoomInventory();

        inventory.displayInventory();

        // Example update
        System.out.println("\nUpdating inventory for Single Room...");
        inventory.updateAvailability("Single Room", 4);

        inventory.displayInventory();
    }
}