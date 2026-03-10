/**
 * Main
 *
 * UC1 + UC2 implementation for the Hotel Booking System.
 * Demonstrates abstraction, inheritance, polymorphism,
 * and simple availability variables.
 *
 * @author Dhruv
 * @version 1.0
 */

public class Main {

    // Abstract Room class
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

    // Single Room
    static class SingleRoom extends Room {

        public SingleRoom() {
            super(1, 20, 100);
        }

        public String getRoomType() {
            return "Single Room";
        }
    }

    // Double Room
    static class DoubleRoom extends Room {

        public DoubleRoom() {
            super(2, 30, 180);
        }

        public String getRoomType() {
            return "Double Room";
        }
    }

    // Suite Room
    static class SuiteRoom extends Room {

        public SuiteRoom() {
            super(3, 50, 350);
        }

        public String getRoomType() {
            return "Suite Room";
        }
    }

    public static void main(String[] args) {

        System.out.println("Welcome to the Hotel Booking System");
        System.out.println("Application: Hotel Booking System");
        System.out.println("Version: v1.0\n");

        System.out.println("Available Room Types:\n");

        // Create room objects (polymorphism)
        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        // Static availability variables
        int singleAvailability = 5;
        int doubleAvailability = 3;
        int suiteAvailability = 2;

        // Display details
        System.out.println(single.getRoomType());
        single.displayDetails();
        System.out.println("Available: " + singleAvailability);
        System.out.println();

        System.out.println(doubleRoom.getRoomType());
        doubleRoom.displayDetails();
        System.out.println("Available: " + doubleAvailability);
        System.out.println();

        System.out.println(suite.getRoomType());
        suite.displayDetails();
        System.out.println("Available: " + suiteAvailability);
    }
}