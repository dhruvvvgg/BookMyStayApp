/**
 * Main
 *
 * Hotel Booking Application
 *
 * UC1–UC6 existing
 * UC7: Add-on services
 */

import java.util.*;

public class Main {

    // UC2: Room Model
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
        public SingleRoom() { super(1, 20, 100); }
        public String getRoomType() { return "Single Room"; }
    }

    static class DoubleRoom extends Room {
        public DoubleRoom() { super(2, 30, 180); }
        public String getRoomType() { return "Double Room"; }
    }

    static class SuiteRoom extends Room {
        public SuiteRoom() { super(3, 50, 350); }
        public String getRoomType() { return "Suite Room"; }
    }

    // UC3: Inventory
    static class RoomInventory {
        private HashMap<String, Integer> inventory;

        public RoomInventory() {
            inventory = new HashMap<>();
            inventory.put("Single Room", 5);
            inventory.put("Double Room", 3);
            inventory.put("Suite Room", 2);
        }

        public int getAvailability(String roomType) {
            return inventory.getOrDefault(roomType, 0);
        }

        public void decreaseAvailability(String roomType) {
            int count = inventory.getOrDefault(roomType, 0);
            if (count > 0) inventory.put(roomType, count - 1);
        }

        public void displayInventory() {
            System.out.println("\nCurrent Inventory:");
            for (Map.Entry<String, Integer> e : inventory.entrySet()) {
                System.out.println(e.getKey() + " → Available: " + e.getValue());
            }
        }
    }

    // UC4: Search
    static class SearchService {
        public void searchAvailableRooms(RoomInventory inventory, Room[] rooms) {
            System.out.println("\nAvailable Rooms:\n");
            for (Room room : rooms) {
                int available = inventory.getAvailability(room.getRoomType());
                if (available > 0) {
                    System.out.println(room.getRoomType());
                    room.displayDetails();
                    System.out.println("Available: " + available + "\n");
                }
            }
        }
    }

    // UC5: Reservation
    static class Reservation {
        private String guestName;
        private String roomType;

        public Reservation(String guestName, String roomType) {
            this.guestName = guestName;
            this.roomType = roomType;
        }

        public String getGuestName() { return guestName; }
        public String getRoomType() { return roomType; }

        public void displayRequest() {
            System.out.println(guestName + " requested " + roomType);
        }
    }

    // UC5: Queue
    static class BookingRequestQueue {
        private Queue<Reservation> queue = new LinkedList<>();

        public void addRequest(Reservation r) {
            queue.add(r);
            System.out.println("Request added:");
            r.displayRequest();
        }

        public Queue<Reservation> getQueue() { return queue; }

        public void displayQueue() {
            System.out.println("\nCurrent Booking Request Queue:");
            for (Reservation r : queue) r.displayRequest();
        }
    }

    // UC6: Booking Service
    static class BookingService {

        private HashMap<String, Set<String>> allocatedRooms = new HashMap<>();
        private Set<String> usedRoomIds = new HashSet<>();

        public List<String> processBookings(BookingRequestQueue requestQueue, RoomInventory inventory) {

            List<String> reservationIds = new ArrayList<>();
            Queue<Reservation> queue = requestQueue.getQueue();

            System.out.println("\nProcessing Booking Requests...\n");

            while (!queue.isEmpty()) {

                Reservation r = queue.poll();
                String roomType = r.getRoomType();

                if (inventory.getAvailability(roomType) > 0) {

                    String roomId = generateRoomId(roomType);

                    allocatedRooms
                            .computeIfAbsent(roomType, k -> new HashSet<>())
                            .add(roomId);

                    usedRoomIds.add(roomId);
                    inventory.decreaseAvailability(roomType);

                    reservationIds.add(roomId);

                    System.out.println("Reservation Confirmed:");
                    System.out.println(r.getGuestName() +
                            " → " + roomType +
                            " | Room ID: " + roomId + "\n");

                } else {
                    System.out.println("Reservation Failed for " + r.getGuestName());
                }
            }
            return reservationIds;
        }

        private String generateRoomId(String roomType) {
            String prefix = roomType.replace(" ", "").substring(0,3).toUpperCase();
            String id;
            do {
                id = prefix + "-" + (usedRoomIds.size() + 1);
            } while (usedRoomIds.contains(id));
            return id;
        }
    }

    // UC7: Add-On Service
    static class AddOnService {
        private String name;
        private double price;

        public AddOnService(String name, double price) {
            this.name = name;
            this.price = price;
        }

        public String getName() { return name; }
        public double getPrice() { return price; }
    }

    static class AddOnServiceManager {

        private HashMap<String, List<AddOnService>> serviceMap = new HashMap<>();

        public void addService(String reservationId, AddOnService service) {
            serviceMap.computeIfAbsent(reservationId, k -> new ArrayList<>()).add(service);
        }

        public void displayServices(String reservationId) {
            List<AddOnService> list = serviceMap.getOrDefault(reservationId, new ArrayList<>());

            System.out.println("\nServices for " + reservationId + ":");

            double total = 0;
            for (AddOnService s : list) {
                System.out.println(s.getName() + " $" + s.getPrice());
                total += s.getPrice();
            }

            System.out.println("Total Add-on Cost: $" + total);
        }
    }

    // Main
    public static void main(String[] args) {

        System.out.println("Welcome to the Hotel Booking System\n");

        Room[] rooms = { new SingleRoom(), new DoubleRoom(), new SuiteRoom() };

        RoomInventory inventory = new RoomInventory();
        inventory.displayInventory();

        new SearchService().searchAvailableRooms(inventory, rooms);

        BookingRequestQueue queue = new BookingRequestQueue();
        queue.addRequest(new Reservation("Alice", "Single Room"));
        queue.addRequest(new Reservation("Bob", "Suite Room"));
        queue.addRequest(new Reservation("Charlie", "Double Room"));
        queue.displayQueue();

        BookingService bookingService = new BookingService();
        List<String> ids = bookingService.processBookings(queue, inventory);

        inventory.displayInventory();

        // UC7 usage
        AddOnServiceManager manager = new AddOnServiceManager();

        AddOnService breakfast = new AddOnService("Breakfast", 20);
        AddOnService wifi = new AddOnService("WiFi", 10);
        AddOnService spa = new AddOnService("Spa", 50);

        if (ids.size() > 0) {
            manager.addService(ids.get(0), breakfast);
            manager.addService(ids.get(0), wifi);
            manager.displayServices(ids.get(0));
        }

        if (ids.size() > 1) {
            manager.addService(ids.get(1), spa);
            manager.displayServices(ids.get(1));
        }
    }
}