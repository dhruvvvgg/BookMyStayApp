import java.util.*;

public class Main {

    // -------------------------
    // UC2: Room Model
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

    // -------------------------
    // UC3: Inventory
    // -------------------------
    static class RoomInventory {
        private HashMap<String, Integer> inventory = new HashMap<>();

        public RoomInventory() {
            inventory.put("Single Room", 5);
            inventory.put("Double Room", 3);
            inventory.put("Suite Room", 2);
        }

        public synchronized int getAvailability(String roomType) {
            return inventory.getOrDefault(roomType, 0);
        }

        public synchronized void decreaseAvailability(String roomType) {
            int count = inventory.getOrDefault(roomType, 0);
            if (count > 0) inventory.put(roomType, count - 1);
        }

        public synchronized void increaseAvailability(String roomType) {
            int count = inventory.getOrDefault(roomType, 0);
            inventory.put(roomType, count + 1);
        }

        public void displayInventory() {
            System.out.println("\nCurrent Inventory:");
            for (Map.Entry<String, Integer> e : inventory.entrySet()) {
                System.out.println(e.getKey() + " -> Available: " + e.getValue());
            }
        }
    }

    // -------------------------
    // UC4: Search Service
    // -------------------------
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

    // -------------------------
    // UC5: Reservation
    // -------------------------
    static class Reservation {
        private String guestName;
        private String roomType;

        public Reservation(String guestName, String roomType) {
            this.guestName = guestName;
            this.roomType = roomType;
        }

        public String getGuestName() { return guestName; }
        public String getRoomType() { return roomType; }
    }

    // -------------------------
    // UC5: Queue
    // -------------------------
    static class BookingRequestQueue {
        private Queue<Reservation> queue = new LinkedList<>();

        public void addRequest(Reservation r) {
            queue.add(r);
        }

        public Queue<Reservation> getQueue() { return queue; }
    }

    // -------------------------
    // UC6: Booking Service
    // -------------------------
    static class BookingService {

        protected Set<String> usedRoomIds = new HashSet<>();

        public List<String> processBookings(BookingRequestQueue requestQueue,
                                            RoomInventory inventory,
                                            BookingHistory history) {

            List<String> reservationIds = new ArrayList<>();
            Queue<Reservation> queue = requestQueue.getQueue();

            System.out.println("\nProcessing Bookings...\n");

            while (!queue.isEmpty()) {

                Reservation r = queue.poll();
                String roomType = r.getRoomType();

                if (inventory.getAvailability(roomType) > 0) {

                    String id = generateRoomId(roomType);

                    usedRoomIds.add(id);
                    inventory.decreaseAvailability(roomType);

                    // UC8: store history
                    history.add(r);

                    reservationIds.add(id);

                    System.out.println("Confirmed: " +
                            r.getGuestName() + " -> " + roomType +
                            " | ID: " + id);

                } else {
                    System.out.println("Failed: " + r.getGuestName());
                }
            }
            return reservationIds;
        }

        protected String generateRoomId(String roomType) {
            String prefix = roomType.substring(0, 3).toUpperCase();
            String id = prefix + "-" + (usedRoomIds.size() + 1);
            usedRoomIds.add(id);
            return id;
        }
    }

    // -------------------------
    // UC7: Add-On Services
    // -------------------------
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

    // -------------------------
    // UC8: Booking History
    // -------------------------
    static class BookingHistory {
        private List<Reservation> history = new ArrayList<>();
        // maps reservation ID -> Reservation for UC10 lookup and cancellation
        private Map<String, Reservation> idMap = new HashMap<>();

        public synchronized void add(String reservationId, Reservation r) {
            history.add(r);
            idMap.put(reservationId, r);
        }

        // kept for backward compatibility with UC6 BookingService
        public synchronized void add(Reservation r) {
            history.add(r);
        }

        public synchronized Reservation getById(String reservationId) {
            return idMap.get(reservationId);
        }

        public synchronized void remove(String reservationId) {
            Reservation r = idMap.remove(reservationId);
            if (r != null) history.remove(r);
        }

        public synchronized List<Reservation> getAll() {
            return new ArrayList<>(history);
        }
    }

    static class BookingReportService {

        public void printAll(List<Reservation> history) {
            System.out.println("\nBooking History:");
            for (Reservation r : history) {
                System.out.println(r.getGuestName() + " -> " + r.getRoomType());
            }
        }

        public void summary(List<Reservation> history) {
            Map<String, Integer> count = new HashMap<>();

            for (Reservation r : history) {
                count.put(r.getRoomType(),
                        count.getOrDefault(r.getRoomType(), 0) + 1);
            }

            System.out.println("\nBooking Summary:");
            for (String type : count.keySet()) {
                System.out.println(type + ": " + count.get(type));
            }
        }
    }

    // -------------------------
    // UC9: Custom Exceptions
    // -------------------------
    static class HotelBookingException extends RuntimeException {
        public HotelBookingException(String message) {
            super(message);
        }
    }

    static class InvalidRoomTypeException extends HotelBookingException {
        public InvalidRoomTypeException(String roomType) {
            super("Invalid room type: \"" + roomType + "\". Must be Single Room, Double Room, or Suite Room.");
        }
    }

    static class InvalidGuestNameException extends HotelBookingException {
        public InvalidGuestNameException() {
            super("Guest name cannot be null or blank.");
        }
    }

    static class InventoryUnderflowException extends HotelBookingException {
        public InventoryUnderflowException(String roomType) {
            super("Cannot decrease inventory for \"" + roomType + "\": no rooms remaining.");
        }
    }

    static class InvalidCancellationException extends HotelBookingException {
        public InvalidCancellationException(String reservationId) {
            super("Cancellation failed: reservation ID \"" + reservationId + "\" not found or already cancelled.");
        }
    }

    // -------------------------
    // UC9: Booking Validator
    // -------------------------
    static class BookingValidator {

        private static final Set<String> VALID_ROOM_TYPES = new HashSet<>(
                Arrays.asList("Single Room", "Double Room", "Suite Room")
        );

        public void validateReservation(Reservation r) {
            if (r.getGuestName() == null || r.getGuestName().trim().isEmpty()) {
                throw new InvalidGuestNameException();
            }
            if (!VALID_ROOM_TYPES.contains(r.getRoomType())) {
                throw new InvalidRoomTypeException(r.getRoomType());
            }
        }

        public void validateInventoryBeforeDecrease(String roomType, int currentCount) {
            if (currentCount <= 0) {
                throw new InventoryUnderflowException(roomType);
            }
        }
    }

    // -------------------------
    // UC9: Validated Booking Service
    // -------------------------
    static class ValidatedBookingService extends BookingService {

        private BookingValidator validator = new BookingValidator();

        @Override
        public List<String> processBookings(BookingRequestQueue requestQueue,
                                            RoomInventory inventory,
                                            BookingHistory history) {

            List<String> reservationIds = new ArrayList<>();
            Queue<Reservation> queue = requestQueue.getQueue();

            System.out.println("\nProcessing Bookings (with Validation)...\n");

            while (!queue.isEmpty()) {

                Reservation r = queue.poll();

                try {
                    // validate input before touching any state
                    validator.validateReservation(r);

                    String roomType = r.getRoomType();

                    // validate inventory state before decrease
                    validator.validateInventoryBeforeDecrease(
                            roomType, inventory.getAvailability(roomType));

                    String id = generateRoomId(roomType);
                    inventory.decreaseAvailability(roomType);
                    history.add(id, r);
                    reservationIds.add(id);

                    System.out.println("Confirmed: " +
                            r.getGuestName() + " -> " + roomType +
                            " | ID: " + id);

                } catch (HotelBookingException e) {
                    // graceful failure: log message and continue safely
                    String name = (r.getGuestName() != null && !r.getGuestName().trim().isEmpty())
                            ? r.getGuestName() : "Unknown Guest";
                    System.out.println("Validation Error [" + name + "]: " + e.getMessage());
                }
            }
            return reservationIds;
        }
    }

    // -------------------------
    // UC10: Cancellation Service
    // -------------------------
    static class CancellationService {

        // Stack tracks released IDs in LIFO order for rollback
        private Stack<String> rollbackStack = new Stack<>();

        public void cancel(String reservationId,
                           RoomInventory inventory,
                           BookingHistory history) {

            // validate: reservation must exist and not already be cancelled
            Reservation r = history.getById(reservationId);
            if (r == null) {
                throw new InvalidCancellationException(reservationId);
            }

            String roomType = r.getRoomType();

            // push ID onto rollback stack before mutating state
            rollbackStack.push(reservationId);

            // restore inventory count
            inventory.increaseAvailability(roomType);

            // remove from booking history
            history.remove(reservationId);

            System.out.println("Cancelled: " + r.getGuestName() +
                    " -> " + roomType + " | ID: " + reservationId);
        }

        public void displayRollbackStack() {
            System.out.println("\nRollback Stack (most recent cancellation on top):");
            if (rollbackStack.isEmpty()) {
                System.out.println("  (empty)");
            } else {
                Stack<String> temp = new Stack<>();
                temp.addAll(rollbackStack);
                while (!temp.isEmpty()) {
                    System.out.println("  " + temp.pop());
                }
            }
        }
    }

    // -------------------------
    // UC11: Shared Booking Queue
    // -------------------------
    static class SharedBookingQueue {

        private final Queue<Reservation> queue = new LinkedList<>();

        public synchronized void addRequest(Reservation r) {
            queue.add(r);
        }

        // returns null when queue is empty — threads use this to know when to stop
        public synchronized Reservation pollRequest() {
            return queue.poll();
        }

        public synchronized boolean isEmpty() {
            return queue.isEmpty();
        }
    }

    // -------------------------
    // UC11: Concurrent Booking Processor
    // -------------------------
    static class ConcurrentBookingProcessor implements Runnable {

        private final SharedBookingQueue sharedQueue;
        private final RoomInventory inventory;
        private final BookingHistory history;
        private final List<String> confirmedIds;
        private final int threadId;
        private static int idCounter = 0;

        public ConcurrentBookingProcessor(SharedBookingQueue sharedQueue,
                                          RoomInventory inventory,
                                          BookingHistory history,
                                          List<String> confirmedIds,
                                          int threadId) {
            this.sharedQueue = sharedQueue;
            this.inventory = inventory;
            this.history = history;
            this.confirmedIds = confirmedIds;
            this.threadId = threadId;
        }

        @Override
        public void run() {
            while (true) {
                Reservation r;

                // critical section: poll and allocate must be atomic together
                // to prevent two threads reading the same availability count
                synchronized (inventory) {
                    r = sharedQueue.pollRequest();
                    if (r == null) break; // queue exhausted

                    String roomType = r.getRoomType();

                    if (inventory.getAvailability(roomType) > 0) {
                        String id = generateId(roomType);
                        inventory.decreaseAvailability(roomType);
                        history.add(id, r);

                        synchronized (confirmedIds) {
                            confirmedIds.add(id);
                        }

                        System.out.println("[Thread-" + threadId + "] Confirmed: " +
                                r.getGuestName() + " -> " + roomType + " | ID: " + id);
                    } else {
                        System.out.println("[Thread-" + threadId + "] No availability: " +
                                r.getGuestName() + " -> " + roomType);
                    }
                }
            }
        }

        private static synchronized String generateId(String roomType) {
            String prefix = roomType.substring(0, 3).toUpperCase();
            return prefix + "-C" + (++idCounter);
        }
    }

    // -------------------------
    // Main
    // -------------------------
    public static void main(String[] args) {

        Room[] rooms = {
                new SingleRoom(),
                new DoubleRoom(),
                new SuiteRoom()
        };

        RoomInventory inventory = new RoomInventory();
        inventory.displayInventory();

        new SearchService().searchAvailableRooms(inventory, rooms);

        BookingRequestQueue queue = new BookingRequestQueue();

        // Valid requests
        queue.addRequest(new Reservation("Alice", "Single Room"));
        queue.addRequest(new Reservation("Bob", "Suite Room"));
        queue.addRequest(new Reservation("Charlie", "Double Room"));

        // UC9: Invalid requests — rejected gracefully
        queue.addRequest(new Reservation("Dave", "Penthouse Suite")); // invalid room type
        queue.addRequest(new Reservation("", "Single Room"));          // blank guest name
        queue.addRequest(new Reservation(null, "Double Room"));        // null guest name

        BookingHistory history = new BookingHistory();

        // UC9: ValidatedBookingService replaces BookingService
        ValidatedBookingService service = new ValidatedBookingService();
        List<String> ids = service.processBookings(queue, inventory, history);

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

        // UC8 reporting (before cancellations)
        BookingReportService report = new BookingReportService();
        report.printAll(history.getAll());
        report.summary(history.getAll());

        // UC10: Cancellation
        System.out.println("\n--- UC10: Cancellations ---");
        CancellationService cancellationService = new CancellationService();

        // valid cancellation
        if (ids.size() > 0) {
            try {
                cancellationService.cancel(ids.get(0), inventory, history);
            } catch (HotelBookingException e) {
                System.out.println("Cancellation Error: " + e.getMessage());
            }
        }

        // duplicate cancellation — same ID, should be rejected
        if (ids.size() > 0) {
            try {
                cancellationService.cancel(ids.get(0), inventory, history);
            } catch (HotelBookingException e) {
                System.out.println("Cancellation Error: " + e.getMessage());
            }
        }

        // non-existent ID
        try {
            cancellationService.cancel("XYZ-999", inventory, history);
        } catch (HotelBookingException e) {
            System.out.println("Cancellation Error: " + e.getMessage());
        }

        cancellationService.displayRollbackStack();
        inventory.displayInventory();

        // UC8 reporting (after cancellations)
        System.out.println("\n--- Booking History After Cancellations ---");
        report.printAll(history.getAll());
        report.summary(history.getAll());

        // -------------------------------------------------------
        // UC11: Concurrent Booking Simulation
        // -------------------------------------------------------
        System.out.println("\n--- UC11: Concurrent Booking Simulation ---");

        // fresh inventory and history for the concurrent scenario
        RoomInventory concurrentInventory = new RoomInventory();
        BookingHistory concurrentHistory = new BookingHistory();
        List<String> concurrentIds = Collections.synchronizedList(new ArrayList<>());

        // shared queue loaded with 10 guests competing for 10 total rooms (5+3+2)
        SharedBookingQueue sharedQueue = new SharedBookingQueue();
        String[] roomTypes = {"Single Room", "Double Room", "Suite Room"};
        for (int i = 1; i <= 10; i++) {
            String type = roomTypes[i % roomTypes.length];
            sharedQueue.addRequest(new Reservation("Guest-" + i, type));
        }
        // 2 extra requests that will be denied due to full inventory
        sharedQueue.addRequest(new Reservation("Guest-11", "Single Room"));
        sharedQueue.addRequest(new Reservation("Guest-12", "Suite Room"));

        // 4 threads compete to process the shared queue
        int numThreads = 4;
        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            threads[i] = new Thread(new ConcurrentBookingProcessor(
                    sharedQueue, concurrentInventory, concurrentHistory, concurrentIds, i + 1));
        }

        // start all threads simultaneously
        for (Thread t : threads) t.start();

        // wait for all threads to finish before reading results
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("\nConcurrent booking complete.");
        concurrentInventory.displayInventory();

        System.out.println("\nConfirmed IDs: " + concurrentIds);
        System.out.println("Total confirmed: " + concurrentIds.size());

        BookingReportService concurrentReport = new BookingReportService();
        concurrentReport.printAll(concurrentHistory.getAll());
        concurrentReport.summary(concurrentHistory.getAll());
    }
}