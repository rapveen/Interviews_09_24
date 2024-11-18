Designing a multi-floor parking lot system that accommodates different vehicle types (cars and bikes) with dynamic parking rules (e.g., allowing 4 bikes in one car spot during busy times) involves careful consideration of object-oriented design principles. Below, we'll explore:

1. **Class Diagram Overview**
2. **Pseudo Class Implementation**
3. **Approaches to Retrieve Available Parking Spots**

---

## **1. Class Diagram Overview**

A class diagram visually represents the system's structure, illustrating classes, their attributes, methods, and relationships. Here's a high-level overview of the classes involved in designing the parking lot system:

### **Key Classes:**

1. **ParkingLot**
2. **Floor**
3. **ParkingSpot**
4. **Vehicle**
5. **Car**
6. **Bike**
7. **ParkingManager**
8. **ParkingStrategy** (for dynamic parking rules)

### **Class Descriptions and Relationships:**

1. **ParkingLot**
   - **Attributes:**
     - `floors: List<Floor>`
     - `parkingManager: ParkingManager`
   - **Methods:**
     - `parkVehicle(vehicle: Vehicle): boolean`
     - `unparkVehicle(vehicleId: String): boolean`
     - `getAvailableSpots(vehicleType: VehicleType): List<ParkingSpot>`
   - **Relationships:**
     - Aggregates multiple `Floor` objects.
     - Uses `ParkingManager` to handle parking logic.

2. **Floor**
   - **Attributes:**
     - `floorNumber: int`
     - `parkingSpots: List<ParkingSpot>`
   - **Methods:**
     - `getAvailableSpots(vehicleType: VehicleType): List<ParkingSpot>`
   - **Relationships:**
     - Contains multiple `ParkingSpot` objects.

3. **ParkingSpot**
   - **Attributes:**
     - `spotId: String`
     - `spotType: SpotType` (e.g., CAR, BIKE)
     - `isOccupied: boolean`
     - `vehicles: List<Vehicle>` (to handle multiple bikes in a car spot)
   - **Methods:**
     - `canFit(vehicle: Vehicle): boolean`
     - `park(vehicle: Vehicle): boolean`
     - `unpark(vehicleId: String): boolean`
   - **Relationships:**
     - May hold one `Car` or multiple `Bike` objects based on `spotType` and current conditions.

4. **Vehicle** (Abstract Class)
   - **Attributes:**
     - `vehicleId: String`
     - `vehicleType: VehicleType` (ENUM: CAR, BIKE)
   - **Methods:**
     - `getSize(): int` (e.g., space required)
   - **Relationships:**
     - Inherited by `Car` and `Bike` classes.

5. **Car** (Inherits from `Vehicle`)
   - **Attributes:**
     - `licensePlate: String`
   - **Methods:**
     - `getSize()`: Typically returns a standard size value.

6. **Bike** (Inherits from `Vehicle`)
   - **Attributes:**
     - `licensePlate: String`
   - **Methods:**
     - `getSize()`: Returns a smaller size value compared to `Car`.

7. **ParkingManager**
   - **Attributes:**
     - `parkingLot: ParkingLot`
     - `currentStrategy: ParkingStrategy`
   - **Methods:**
     - `parkVehicle(vehicle: Vehicle): boolean`
     - `unparkVehicle(vehicleId: String): boolean`
     - `setStrategy(strategy: ParkingStrategy): void`
   - **Relationships:**
     - Interacts with `ParkingLot` to manage parking operations.
     - Utilizes `ParkingStrategy` to apply different parking rules.

8. **ParkingStrategy** (Interface or Abstract Class)
   - **Methods:**
     - `findAvailableSpot(parkingLot: ParkingLot, vehicle: Vehicle): ParkingSpot`
   - **Implementations:**
     - **NormalParkingStrategy:** Standard parking rules.
     - **BusyTimeParkingStrategy:** Allows multiple bikes in a car spot.

### **Class Diagram Illustration:**

While a textual description provides an overview, here's a simplified representation of the relationships:

```
+----------------+          +----------------+
|   ParkingLot   |<>--------|      Floor     |
+----------------+          +----------------+
| - floors: List |          | - floorNumber  |
| - manager      |          | - spots: List  |
+----------------+          +----------------+
| + parkVehicle()|          | + getAvailable()|
| + unparkVehicle()|        +----------------+
| + getAvailableSpots()|
+----------------+

+----------------+          +----------------+          +----------------+
|   ParkingManager|<>-------|  ParkingStrategy|<>-------|NormalStrategy  |
+----------------+          +----------------+          |BusyTimeStrategy |
| - parkingLot    |          | + findSpot()   |          +----------------+
| - currentStrategy|        +----------------+
+----------------+
| + parkVehicle()|
| + unparkVehicle()|
| + setStrategy()|
+----------------+

+----------------+          +----------------+
|    ParkingSpot  |<>--------|    Vehicle     |
+----------------+          +----------------+
| - spotId       |          | - vehicleId    |
| - spotType     |          | - vehicleType  |
| - isOccupied   |          +----------------+
| - vehicles     |          | + getSize()    |
+----------------+          +----------------+
| + canFit()     |
| + park()       |
| + unpark()     |
+----------------+

         ^                       
         |                       
 +----------------+               
 |      Car       |               
 +----------------+               
 | - licensePlate |               
 +----------------+               
 | + getSize()    |               
 +----------------+               

 +----------------+              
 |      Bike      |              
 +----------------+              
 | - licensePlate |              
 +----------------+              
 | + getSize()    |              
 +----------------+
```

---

## **2. Pseudo Class Implementation**

Below is a pseudo-code representation of the key classes discussed. This implementation abstracts language-specific syntax and focuses on the structure and relationships.

### **Enums for Spot and Vehicle Types**

```pseudo
enum SpotType {
    CAR,
    BIKE
}

enum VehicleType {
    CAR,
    BIKE
}
```

### **Vehicle Class Hierarchy**

```pseudo
abstract class Vehicle {
    String vehicleId
    VehicleType vehicleType

    abstract int getSize()
}

class Car extends Vehicle {
    String licensePlate

    Car(String vehicleId, String licensePlate) {
        this.vehicleId = vehicleId
        this.vehicleType = VehicleType.CAR
        this.licensePlate = licensePlate
    }

    int getSize() {
        return 4 // Arbitrary size unit for a car
    }
}

class Bike extends Vehicle {
    String licensePlate

    Bike(String vehicleId, String licensePlate) {
        this.vehicleId = vehicleId
        this.vehicleType = VehicleType.BIKE
        this.licensePlate = licensePlate
    }

    int getSize() {
        return 1 // Arbitrary size unit for a bike
    }
}
```

### **ParkingSpot Class**

```pseudo
class ParkingSpot {
    String spotId
    SpotType spotType
    boolean isOccupied
    List<Vehicle> vehicles
    int capacity // Number of bikes that can fit if spotType is CAR

    ParkingSpot(String spotId, SpotType spotType) {
        this.spotId = spotId
        this.spotType = spotType
        this.isOccupied = false
        this.vehicles = emptyList()
        if (spotType == SpotType.CAR) {
            this.capacity = 4 // Allow 4 bikes in a car spot during busy times
        } else {
            this.capacity = 1 // Standard capacity for bikes
        }
    }

    boolean canFit(Vehicle vehicle) {
        if (spotType == SpotType.CAR) {
            if (vehicle.vehicleType == VehicleType.CAR) {
                return !isOccupied
            } else if (vehicle.vehicleType == VehicleType.BIKE) {
                return vehicles.size() < capacity
            }
        } else if (spotType == SpotType.BIKE) {
            return vehicle.vehicleType == VehicleType.BIKE && !isOccupied
        }
        return false
    }

    boolean park(Vehicle vehicle) {
        if (canFit(vehicle)) {
            vehicles.add(vehicle)
            if (vehicle.vehicleType == VehicleType.CAR || vehicles.size() == capacity) {
                isOccupied = true
            }
            return true
        }
        return false
    }

    boolean unpark(String vehicleId) {
        for (vehicle in vehicles) {
            if (vehicle.vehicleId == vehicleId) {
                vehicles.remove(vehicle)
                if (spotType == SpotType.CAR && vehicles.size() < capacity) {
                    isOccupied = false
                } else if (spotType == SpotType.BIKE) {
                    isOccupied = false
                }
                return true
            }
        }
        return false
    }

    int getAvailableCapacity() {
        if (spotType == SpotType.CAR) {
            return capacity - vehicles.size()
        } else if (spotType == SpotType.BIKE) {
            return isOccupied ? 0 : 1
        }
        return 0
    }
}
```

### **Floor Class**

```pseudo
class Floor {
    int floorNumber
    List<ParkingSpot> parkingSpots

    Floor(int floorNumber) {
        this.floorNumber = floorNumber
        this.parkingSpots = emptyList()
    }

    void addParkingSpot(ParkingSpot spot) {
        parkingSpots.add(spot)
    }

    List<ParkingSpot> getAvailableSpots(VehicleType vehicleType) {
        availableSpots = emptyList()
        for (spot in parkingSpots) {
            if (spot.canFit(vehicleTypeToVehicle(vehicleType))) {
                availableSpots.add(spot)
            }
        }
        return availableSpots
    }

    Vehicle vehicleTypeToVehicle(VehicleType type) {
        if (type == VehicleType.CAR) {
            return new Car("temp", "temp")
        } else {
            return new Bike("temp", "temp")
        }
    }
}
```

### **ParkingStrategy Interface and Implementations**

```pseudo
interface ParkingStrategy {
    ParkingSpot findAvailableSpot(ParkingLot parkingLot, Vehicle vehicle)
}

class NormalParkingStrategy implements ParkingStrategy {
    ParkingSpot findAvailableSpot(ParkingLot parkingLot, Vehicle vehicle) {
        for (floor in parkingLot.floors) {
            for (spot in floor.parkingSpots) {
                if (spot.canFit(vehicle)) {
                    return spot
                }
            }
        }
        return null
    }
}

class BusyTimeParkingStrategy implements ParkingStrategy {
    ParkingSpot findAvailableSpot(ParkingLot parkingLot, Vehicle vehicle) {
        if (vehicle.vehicleType == VehicleType.BIKE) {
            // Try to find bike spot first
            for (floor in parkingLot.floors) {
                for (spot in floor.parkingSpots) {
                    if (spot.spotType == SpotType.BIKE && spot.canFit(vehicle)) {
                        return spot
                    }
                }
            }
            // If no bike spot, try to find a car spot with available bike capacity
            for (floor in parkingLot.floors) {
                for (spot in floor.parkingSpots) {
                    if (spot.spotType == SpotType.CAR && spot.canFit(vehicle)) {
                        return spot
                    }
                }
            }
        } else if (vehicle.vehicleType == VehicleType.CAR) {
            // Only car spots can accommodate cars
            for (floor in parkingLot.floors) {
                for (spot in floor.parkingSpots) {
                    if (spot.canFit(vehicle)) {
                        return spot
                    }
                }
            }
        }
        return null
    }
}
```

### **ParkingManager Class**

```pseudo
class ParkingManager {
    ParkingLot parkingLot
    ParkingStrategy currentStrategy

    ParkingManager(ParkingLot parkingLot, ParkingStrategy strategy) {
        this.parkingLot = parkingLot
        this.currentStrategy = strategy
    }

    void setStrategy(ParkingStrategy strategy) {
        this.currentStrategy = strategy
    }

    boolean parkVehicle(Vehicle vehicle) {
        spot = currentStrategy.findAvailableSpot(parkingLot, vehicle)
        if (spot != null) {
            return spot.park(vehicle)
        }
        return false
    }

    boolean unparkVehicle(String vehicleId) {
        for (floor in parkingLot.floors) {
            for (spot in floor.parkingSpots) {
                if (spot.unpark(vehicleId)) {
                    return true
                }
            }
        }
        return false
    }

    List<ParkingSpot> getAvailableSpots(VehicleType vehicleType) {
        availableSpots = emptyList()
        for (floor in parkingLot.floors) {
            availableSpots.addAll(floor.getAvailableSpots(vehicleType))
        }
        return availableSpots
    }
}
```

### **ParkingLot Class**

```pseudo
class ParkingLot {
    List<Floor> floors
    ParkingManager parkingManager

    ParkingLot(int numberOfFloors, int spotsPerFloor) {
        this.floors = emptyList()
        for (i from 1 to numberOfFloors) {
            floor = new Floor(i)
            for (j from 1 to spotsPerFloor) {
                // Example distribution: 70% car spots, 30% bike spots
                if (j <= spotsPerFloor * 0.7) {
                    spot = new ParkingSpot("F" + i + "S" + j, SpotType.CAR)
                } else {
                    spot = new ParkingSpot("F" + i + "S" + j, SpotType.BIKE)
                }
                floor.addParkingSpot(spot)
            }
            floors.add(floor)
        }
        // Initialize with NormalParkingStrategy
        this.parkingManager = new ParkingManager(this, new NormalParkingStrategy())
    }

    boolean parkVehicle(Vehicle vehicle) {
        return parkingManager.parkVehicle(vehicle)
    }

    boolean unparkVehicle(String vehicleId) {
        return parkingManager.unparkVehicle(vehicleId)
    }

    List<ParkingSpot> getAvailableSpots(VehicleType vehicleType) {
        return parkingManager.getAvailableSpots(vehicleType)
    }
}
```

---

## **3. Approaches to Retrieve Available Parking Spots**

Efficient retrieval of available parking spots is critical, especially in large parking lots. Below, we'll explore different strategies ranging from brute-force to optimized solutions.

### **a. Brute-Force Approach**

**Description:**
- Iterate through every floor and every spot to find available spots that fit the vehicle.

**Pros:**
- Simple to implement.
- No additional data structures required.

**Cons:**
- Inefficient for large parking lots.
- High time complexity (\(O(F \times S)\)), where \(F\) is the number of floors and \(S\) is the number of spots per floor.

**Pseudo Implementation:**
```pseudo
function findAvailableSpotsBruteForce(parkingLot, vehicleType):
    availableSpots = emptyList()
    for floor in parkingLot.floors:
        for spot in floor.parkingSpots:
            if spot.canFit(vehicleTypeToVehicle(vehicleType)):
                availableSpots.add(spot)
    return availableSpots
```

### **b. Indexed Approach Using Separate Lists**

**Description:**
- Maintain separate lists or maps for different spot types and their availability.
- For example, have a list for available car spots and another for available bike spots.

**Pros:**
- Reduces the search space.
- Faster retrieval as it avoids checking all spots.

**Cons:**
- Requires additional memory.
- Needs careful synchronization when updating spot availability.

**Pseudo Implementation:**
```pseudo
class ParkingLot:
    List<Floor> floors
    List<ParkingSpot> availableCarSpots
    List<ParkingSpot> availableBikeSpots
    ParkingManager parkingManager

    ParkingLot(int numberOfFloors, int spotsPerFloor):
        // Initialize floors and spots
        for each floor and spot:
            if spotType == CAR:
                availableCarSpots.add(spot)
            else:
                availableBikeSpots.add(spot)
        // Initialize ParkingManager with indexed lists
        parkingManager = new ParkingManager(this, new IndexedParkingStrategy())

class IndexedParkingStrategy implements ParkingStrategy:
    ParkingSpot findAvailableSpot(ParkingLot parkingLot, Vehicle vehicle):
        if (vehicle.vehicleType == VehicleType.CAR):
            if (!parkingLot.availableCarSpots.isEmpty()):
                return parkingLot.availableCarSpots.get(0)
        else if (vehicle.vehicleType == VehicleType.BIKE):
            if (!parkingLot.availableBikeSpots.isEmpty()):
                return parkingLot.availableBikeSpots.get(0)
            // Optionally, check car spots with available bike capacity
            for (spot in parkingLot.availableCarSpots):
                if (spot.getAvailableCapacity() > 0):
                    return spot
        return null
```

### **c. Using Priority Queues or Heaps**

**Description:**
- Implement priority queues to prioritize spots based on criteria (e.g., proximity to entrance, spot size).

**Pros:**
- Enables efficient retrieval of the "best" available spot.
- Optimizes for user convenience (e.g., closest spot).

**Cons:**
- More complex to implement.
- Requires dynamic updates as spots are occupied or freed.

**Pseudo Implementation:**
```pseudo
class ParkingLot:
    PriorityQueue<ParkingSpot> carSpotQueue
    PriorityQueue<ParkingSpot> bikeSpotQueue

    ParkingLot(int numberOfFloors, int spotsPerFloor):
        // Initialize priority queues with comparator based on criteria
        carSpotQueue = new PriorityQueue<>(comparatorByProximity)
        bikeSpotQueue = new PriorityQueue<>(comparatorByProximity)
        // Add spots to respective queues
        for each floor and spot:
            if (spotType == CAR):
                carSpotQueue.add(spot)
            else if (spotType == BIKE):
                bikeSpotQueue.add(spot)

class ProximityComparator implements Comparator<ParkingSpot>:
    int compare(ParkingSpot a, ParkingSpot b):
        return a.distanceToEntrance - b.distanceToEntrance

class PriorityQueueParkingStrategy implements ParkingStrategy:
    ParkingSpot findAvailableSpot(ParkingLot parkingLot, Vehicle vehicle):
        if (vehicle.vehicleType == VehicleType.CAR):
            while (!parkingLot.carSpotQueue.isEmpty()):
                spot = parkingLot.carSpotQueue.peek()
                if (spot.canFit(vehicle)):
                    return spot
                else:
                    parkingLot.carSpotQueue.poll() // Remove unavailable spot
        else if (vehicle.vehicleType == VehicleType.BIKE):
            while (!parkingLot.bikeSpotQueue.isEmpty()):
                spot = parkingLot.bikeSpotQueue.peek()
                if (spot.canFit(vehicle)):
                    return spot
                else:
                    parkingLot.bikeSpotQueue.poll()
            // Optionally check car spots
            for (spot in parkingLot.carSpotQueue):
                if (spot.canFit(vehicle)):
                    return spot
        return null
```

### **d. Spatial Partitioning with Hash Maps or Grids**

**Description:**
- Use hash maps or grid-based indexing to partition parking spots based on location or other attributes, allowing faster access.

**Pros:**
- Highly efficient for large and spatially distributed parking lots.
- Enables quick lookups based on spatial queries.

**Cons:**
- Complex to implement.
- Requires maintenance of spatial indices.

**Pseudo Implementation:**
```pseudo
class ParkingLot:
    HashMap<String, List<ParkingSpot>> spotTypeMap

    ParkingLot(int numberOfFloors, int spotsPerFloor):
        spotTypeMap = new HashMap<>()
        spotTypeMap.put("CAR", emptyList())
        spotTypeMap.put("BIKE", emptyList())
        // Assign spots to respective lists
        for each floor and spot:
            spotTypeMap.get(spot.spotType).add(spot)

class SpatialParkingStrategy implements ParkingStrategy:
    ParkingSpot findAvailableSpot(ParkingLot parkingLot, Vehicle vehicle):
        list = parkingLot.spotTypeMap.get(vehicle.vehicleType)
        for (spot in list):
            if (spot.canFit(vehicle)):
                return spot
        // Optionally, check car spots for bikes
        if (vehicle.vehicleType == VehicleType.BIKE):
            list = parkingLot.spotTypeMap.get("CAR")
            for (spot in list):
                if (spot.canFit(vehicle)):
                    return spot
        return null
```

### **e. Caching Available Spots**

**Description:**
- Maintain a cache or a separate data structure that keeps track of available spots, updating it as vehicles park and unpark.

**Pros:**
- Fast retrieval as the cache directly holds available spots.
- Reduces the need to traverse the entire parking lot structure.

**Cons:**
- Requires synchronization with the main parking lot state.
- Potential consistency issues if not managed properly.

**Pseudo Implementation:**
```pseudo
class ParkingLot:
    List<ParkingSpot> availableSpotsCache

    ParkingLot(int numberOfFloors, int spotsPerFloor):
        availableSpotsCache = emptyList()
        // Initialize and add all spots to the cache
        for each floor and spot:
            availableSpotsCache.add(spot)

    void updateCacheOnPark(ParkingSpot spot):
        if (!spot.canFitAnyMoreVehicles()):
            availableSpotsCache.remove(spot)

    void updateCacheOnUnpark(ParkingSpot spot):
        if (spot.canFitAnyMoreVehicles() && !availableSpotsCache.contains(spot)):
            availableSpotsCache.add(spot)

class CachedParkingStrategy implements ParkingStrategy:
    ParkingSpot findAvailableSpot(ParkingLot parkingLot, Vehicle vehicle):
        for (spot in parkingLot.availableSpotsCache):
            if (spot.canFit(vehicle)):
                return spot
        // Optionally, fallback to searching all spots
        return null
```

---

## **Comparative Analysis of Approaches**

| **Approach**               | **Time Complexity**          | **Space Complexity**      | **Pros**                                      | **Cons**                                       |
|----------------------------|------------------------------|---------------------------|-----------------------------------------------|------------------------------------------------|
| **Brute-Force**            | \(O(F \times S)\)            | \(O(1)\)                  | Simple implementation                        | Inefficient for large parking lots             |
| **Indexed Lists**          | \(O(F \times S)\) to \(O(1)\)| \(O(F \times S)\)         | Faster retrieval, reduced search space       | Increased memory usage, synchronization needed |
| **Priority Queues**        | \(O(\log S)\) per operation   | \(O(S)\)                  | Retrieves best spots efficiently              | Complex implementation, dynamic updates        |
| **Spatial Partitioning**   | \(O(1)\) to \(O(\log S)\)    | \(O(S)\)                  | Highly efficient for spatial queries          | Complex, maintenance of spatial indices        |
| **Caching Available Spots**| \(O(1)\) for retrieval        | \(O(S)\)                  | Instant access to available spots             | Requires careful cache management              |

### **Choosing the Right Approach**

- **Small to Medium Parking Lots:** Brute-force or Indexed Lists are sufficient.
- **Large or High-Traffic Parking Lots:** Priority Queues, Spatial Partitioning, or Caching provide better performance.
- **Dynamic Environments:** Use caching or priority queues to handle frequent updates efficiently.

---

## **4. Implementation Example: Efficient Retrieval Using Indexed Lists**

To illustrate an efficient solution, let's implement the **Indexed Approach** where we maintain separate lists for available car and bike spots. This method balances simplicity and performance, making it suitable for many real-world applications.

### **Pseudo-Code Implementation**

```pseudo
enum SpotType {
    CAR,
    BIKE
}

enum VehicleType {
    CAR,
    BIKE
}

abstract class Vehicle {
    String vehicleId
    VehicleType vehicleType

    abstract int getSize()
}

class Car extends Vehicle {
    String licensePlate

    Car(String vehicleId, String licensePlate) {
        this.vehicleId = vehicleId
        this.vehicleType = VehicleType.CAR
        this.licensePlate = licensePlate
    }

    int getSize() {
        return 4
    }
}

class Bike extends Vehicle {
    String licensePlate

    Bike(String vehicleId, String licensePlate) {
        this.vehicleId = vehicleId
        this.vehicleType = VehicleType.BIKE
        this.licensePlate = licensePlate
    }

    int getSize() {
        return 1
    }
}

class ParkingSpot {
    String spotId
    SpotType spotType
    boolean isOccupied
    List<Vehicle> vehicles
    int capacity

    ParkingSpot(String spotId, SpotType spotType) {
        this.spotId = spotId
        this.spotType = spotType
        this.isOccupied = false
        this.vehicles = emptyList()
        if (spotType == SpotType.CAR) {
            this.capacity = 4
        } else {
            this.capacity = 1
        }
    }

    boolean canFit(Vehicle vehicle) {
        if (spotType == SpotType.CAR) {
            if (vehicle.vehicleType == VehicleType.CAR) {
                return !isOccupied
            } else if (vehicle.vehicleType == VehicleType.BIKE) {
                return vehicles.size() < capacity
            }
        } else if (spotType == SpotType.BIKE) {
            return vehicle.vehicleType == VehicleType.BIKE && !isOccupied
        }
        return false
    }

    boolean park(Vehicle vehicle) {
        if (canFit(vehicle)) {
            vehicles.add(vehicle)
            if (vehicle.vehicleType == VehicleType.CAR || vehicles.size() == capacity) {
                isOccupied = true
            }
            return true
        }
        return false
    }

    boolean unpark(String vehicleId) {
        for (vehicle in vehicles) {
            if (vehicle.vehicleId == vehicleId) {
                vehicles.remove(vehicle)
                if (spotType == SpotType.CAR && vehicles.size() < capacity) {
                    isOccupied = false
                } else if (spotType == SpotType.BIKE) {
                    isOccupied = false
                }
                return true
            }
        }
        return false
    }
}

class Floor {
    int floorNumber
    List<ParkingSpot> parkingSpots

    Floor(int floorNumber) {
        this.floorNumber = floorNumber
        this.parkingSpots = emptyList()
    }

    void addParkingSpot(ParkingSpot spot) {
        parkingSpots.add(spot)
    }
}

class ParkingLot {
    List<Floor> floors
    ParkingManager parkingManager
    List<ParkingSpot> availableCarSpots
    List<ParkingSpot> availableBikeSpots

    ParkingLot(int numberOfFloors, int spotsPerFloor) {
        this.floors = emptyList()
        this.availableCarSpots = emptyList()
        this.availableBikeSpots = emptyList()
        for (i from 1 to numberOfFloors) {
            floor = new Floor(i)
            for (j from 1 to spotsPerFloor) {
                if (j <= spotsPerFloor * 0.7) { // 70% car spots
                    spot = new ParkingSpot("F" + i + "S" + j, SpotType.CAR)
                    availableCarSpots.add(spot)
                } else { // 30% bike spots
                    spot = new ParkingSpot("F" + i + "S" + j, SpotType.BIKE)
                    availableBikeSpots.add(spot)
                }
                floor.addParkingSpot(spot)
            }
            floors.add(floor)
        }
        this.parkingManager = new ParkingManager(this, new IndexedParkingStrategy())
    }
}

interface ParkingStrategy {
    ParkingSpot findAvailableSpot(ParkingLot parkingLot, Vehicle vehicle)
}

class IndexedParkingStrategy implements ParkingStrategy {
    ParkingSpot findAvailableSpot(ParkingLot parkingLot, Vehicle vehicle) {
        if (vehicle.vehicleType == VehicleType.CAR) {
            if (!parkingLot.availableCarSpots.isEmpty()) {
                return parkingLot.availableCarSpots.get(0)
            }
        } else if (vehicle.vehicleType == VehicleType.BIKE) {
            if (!parkingLot.availableBikeSpots.isEmpty()) {
                return parkingLot.availableBikeSpots.get(0)
            }
            // Attempt to park in car spots if available
            for (spot in parkingLot.availableCarSpots) {
                if (spot.canFit(vehicle)) {
                    return spot
                }
            }
        }
        return null
    }
}

class ParkingManager {
    ParkingLot parkingLot
    ParkingStrategy currentStrategy

    ParkingManager(ParkingLot parkingLot, ParkingStrategy strategy) {
        this.parkingLot = parkingLot
        this.currentStrategy = strategy
    }

    void setStrategy(ParkingStrategy strategy) {
        this.currentStrategy = strategy
    }

    boolean parkVehicle(Vehicle vehicle) {
        spot = currentStrategy.findAvailableSpot(parkingLot, vehicle)
        if (spot != null && spot.park(vehicle)) {
            // Update available spots lists
            if (spot.spotType == SpotType.CAR) {
                if (vehicle.vehicleType == VehicleType.CAR || spot.isOccupied) {
                    parkingLot.availableCarSpots.remove(spot)
                }
            } else if (spot.spotType == SpotType.BIKE) {
                if (spot.isOccupied) {
                    parkingLot.availableBikeSpots.remove(spot)
                }
            }
            return true
        }
        return false
    }

    boolean unparkVehicle(String vehicleId) {
        for (floor in parkingLot.floors) {
            for (spot in floor.parkingSpots) {
                if (spot.unpark(vehicleId)) {
                    // Update available spots lists
                    if (spot.spotType == SpotType.CAR) {
                        if (!parkingLot.availableCarSpots.contains(spot)) {
                            parkingLot.availableCarSpots.add(spot)
                        }
                    } else if (spot.spotType == SpotType.BIKE) {
                        if (!parkingLot.availableBikeSpots.contains(spot)) {
                            parkingLot.availableBikeSpots.add(spot)
                        }
                    }
                    return true
                }
            }
        }
        return false
    }

    List<ParkingSpot> getAvailableSpots(VehicleType vehicleType) {
        if (vehicleType == VehicleType.CAR) {
            return parkingLot.availableCarSpots
        } else if (vehicleType == VehicleType.BIKE) {
            available = copy(parkingLot.availableBikeSpots)
            for (spot in parkingLot.availableCarSpots) {
                if (spot.getAvailableCapacity() > 0) {
                    available.add(spot)
                }
            }
            return available
        }
        return emptyList()
    }
}
```

### **Explanation:**

- **ParkingLot Initialization:**
  - Distributes parking spots across floors with a 70% to 30% split between car and bike spots.
  - Maintains separate lists (`availableCarSpots`, `availableBikeSpots`) for efficient retrieval.

- **ParkingManager:**
  - Utilizes `IndexedParkingStrategy` to find available spots.
  - Updates the available spots lists upon parking and unparking vehicles.

- **ParkingStrategy:**
  - `IndexedParkingStrategy` checks `availableCarSpots` and `availableBikeSpots` for efficient spot allocation.

- **ParkingSpot:**
  - Manages occupancy and accommodates multiple bikes in car spots based on capacity.

---

## **5. Conclusion**

Designing a multi-floor parking lot system involves:

1. **Object-Oriented Design:**
   - Defining clear classes and relationships to model real-world entities like parking spots, floors, and vehicles.

2. **Efficient Data Structures:**
   - Implementing indexed lists, priority queues, or caching mechanisms to optimize the retrieval of available parking spots.

3. **Dynamic Parking Rules:**
   - Utilizing strategies (e.g., `ParkingStrategy` interface) to adapt to different parking scenarios, such as allowing multiple bikes in a car spot during busy times.

4. **Scalability and Maintainability:**
   - Ensuring the system can handle large numbers of floors and spots without performance degradation.
   - Facilitating easy updates to parking rules or spot allocations through strategy patterns.

By adopting these design principles and implementation strategies, the parking lot system can efficiently manage vehicle parking, adapt to varying demands, and provide a seamless experience for users.

---

## **Additional Enhancements and Considerations**

1. **Concurrency Handling:**
   - In real-world scenarios, multiple vehicles may attempt to park or unpark simultaneously. Implement thread-safe mechanisms to prevent race conditions, such as using locks or atomic operations.

2. **Real-Time Updates:**
   - Integrate real-time notifications or APIs to inform users about available spots, possibly using WebSockets or server-sent events.

3. **User Interface Integration:**
   - Develop frontend components that interact with the backend to display available spots, allow vehicle parking/unparking, and visualize parking lot occupancy.

4. **Persistence Layer:**
   - Implement a database to persist parking lot state, ensuring data consistency across server restarts and distributed systems.

5. **Advanced Search and Allocation:**
   - Incorporate algorithms that optimize spot allocation based on various factors like vehicle size, proximity to exit, or user preferences.

6. **Reporting and Analytics:**
   - Generate reports on parking lot usage, peak times, and occupancy rates to aid in operational decision-making.

---

**Feel free to ask if you need further details on any specific part, such as concurrency management, real-time updates, or database integration!**