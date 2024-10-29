Certainly! Designing a robust **Database Schema** and a comprehensive **API Design** is crucial for building a scalable and maintainable Bike Rental Shop application. Below, I'll provide detailed designs for both, ensuring they align with the refactored code structure we've discussed.

---

## **1. Database Schema Design**

### **1.1. Overview**

The database schema will consist of the following primary entities:

1. **Customer**
2. **Product** (with subtypes **Bike** and **Scooter**)
3. **Rental**
4. **Charge**

We'll utilize **Single Table Inheritance** for the `Product` entity to simplify the schema, distinguishing between `Bike` and `Scooter` using a discriminator column.

### **1.2. Entity Relationship Diagram (ERD)**

Here's a simplified ERD to visualize the relationships:

```
Customer
---------
id (PK)
name
size
balance
email
phone
created_at
updated_at

Product
---------
id (PK)
name
product_type
base_price
is_available
description
last_maintenance_date
size (nullable)      -- For Bikes
type (nullable)      -- For Scooters
created_at
updated_at

Rental
---------
id (PK)
product_id (FK -> Product.id)
customer_id (FK -> Customer.id)
rented_at
due_date
returned_at
is_returned
created_at
updated_at

Charge
---------
id (PK)
customer_id (FK -> Customer.id)
amount
description
charged_at
created_at
updated_at
```

### **1.3. Table Definitions**

#### **1.3.1. Customer Table**

```sql
CREATE TABLE Customer (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    size VARCHAR(10) NOT NULL, -- ENUM: SMALL, MEDIUM, LARGE
    balance DECIMAL(10, 2) DEFAULT 0.00,
    email VARCHAR(255),
    phone VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

#### **1.3.2. Product Table (Single Table Inheritance)**

```sql
CREATE TABLE Product (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    product_type VARCHAR(50) NOT NULL, -- e.g., "Bike", "Scooter-ELECTRIC"
    base_price DECIMAL(10, 2) NOT NULL,
    is_available BOOLEAN DEFAULT TRUE,
    description TEXT,
    last_maintenance_date TIMESTAMP,
    size VARCHAR(10), -- ENUM: SMALL, MEDIUM, LARGE (applicable to Bikes)
    type VARCHAR(20), -- ENUM: ELECTRIC, GAS (applicable to Scooters)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

**Notes:**

- **Inheritance Strategy**: Using Single Table Inheritance allows us to store both `Bike` and `Scooter` in the same table, differentiating them using the `product_type` column.
- **Nullable Fields**: `size` is applicable only to `Bike`, and `type` is applicable only to `Scooter`.

#### **1.3.3. Rental Table**

```sql
CREATE TABLE Rental (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    rented_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    due_date TIMESTAMP NOT NULL,
    returned_at TIMESTAMP,
    is_returned BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES Product(id),
    FOREIGN KEY (customer_id) REFERENCES Customer(id)
);
```

#### **1.3.4. Charge Table**

```sql
CREATE TABLE Charge (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    description VARCHAR(255),
    charged_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES Customer(id)
);
```

### **1.4. Indexes and Constraints**

- **Indexes**: Create indexes on frequently queried columns such as `Product.product_type`, `Rental.due_date`, `Rental.is_returned`, etc., to optimize query performance.

```sql
CREATE INDEX idx_product_type ON Product(product_type);
CREATE INDEX idx_rental_due_date ON Rental(due_date);
CREATE INDEX idx_rental_is_returned ON Rental(is_returned);
CREATE INDEX idx_customer_balance ON Customer(balance);
```

- **Constraints**: Enforce data integrity using foreign keys and appropriate data types.

---

## **2. API Design**

### **2.1. Overview**

We'll design a **RESTful API** that covers all required queries and commands. The API will follow standard HTTP methods and status codes for operations.

### **2.2. Base URL**

```
https://api.bikerentalshop.com/v1
```

### **2.3. API Endpoints**

#### **2.3.1. Customer Endpoints**

1. **Add a Customer**

   - **Endpoint**: `POST /customers`
   - **Description**: Add a new customer to the system.
   - **Request Body**:

     ```json
     {
       "name": "John Doe",
       "size": "MEDIUM",
       "email": "john.doe@example.com",
       "phone": "123-456-7890"
     }
     ```

   - **Response**:

     - **Status**: `201 Created`
     - **Body**:

       ```json
       {
         "id": "uuid",
         "name": "John Doe",
         "size": "MEDIUM",
         "balance": 0.00,
         "email": "john.doe@example.com",
         "phone": "123-456-7890",
         "created_at": "2024-10-29T12:34:56Z",
         "updated_at": "2024-10-29T12:34:56Z"
       }
       ```

2. **Get Customer Balance**

   - **Endpoint**: `GET /customers/{customerId}/balance`
   - **Description**: Retrieve the balance owed by a customer.
   - **Parameters**:
     - `customerId` (Path Parameter): UUID of the customer.
   - **Response**:

     - **Status**: `200 OK`
     - **Body**:

       ```json
       {
         "customerId": "uuid",
         "balance": 25.50
       }
       ```

3. **Get Customer Rentals**

   - **Endpoint**: `GET /customers/{customerId}/rentals`
   - **Description**: Retrieve all rentals associated with a customer.
   - **Parameters**:
     - `customerId` (Path Parameter): UUID of the customer.
   - **Response**:

     - **Status**: `200 OK`
     - **Body**:

       ```json
       [
         {
           "rentalId": "uuid",
           "productId": "uuid",
           "productName": "Mountain Bike",
           "productType": "Bike-MEDIUM",
           "rentedAt": "2024-10-20T10:00:00Z",
           "dueDate": "2024-10-27T10:00:00Z",
           "isOverdue": false
         },
         {
           "rentalId": "uuid",
           "productId": "uuid",
           "productName": "Electric Scooter",
           "productType": "Scooter-ELECTRIC",
           "rentedAt": "2024-09-15T09:30:00Z",
           "dueDate": "2024-09-22T09:30:00Z",
           "isOverdue": true
         }
       ]
       ```

#### **2.3.2. Product Endpoints**

1. **Add a Product**

   - **Endpoint**: `POST /products`
   - **Description**: Add a new product (Bike or Scooter) to the inventory.
   - **Request Body**:

     ```json
     {
       "name": "Mountain Bike",
       "productType": "Bike",
       "basePrice": 15.00,
       "description": "A sturdy mountain bike.",
       "size": "MEDIUM" // Required for Bikes
     }
     ```

     **Or for Scooters:**

     ```json
     {
       "name": "Electric Scooter",
       "productType": "Scooter",
       "basePrice": 20.00,
       "description": "A fast electric scooter.",
       "type": "ELECTRIC" // Required for Scooters
     }
     ```

   - **Response**:

     - **Status**: `201 Created`
     - **Body**:

       ```json
       {
         "id": "uuid",
         "name": "Mountain Bike",
         "productType": "Bike-MEDIUM",
         "basePrice": 15.00,
         "isAvailable": true,
         "description": "A sturdy mountain bike.",
         "lastMaintenanceDate": "2024-10-29T12:34:56Z",
         "size": "MEDIUM",
         "type": null,
         "created_at": "2024-10-29T12:34:56Z",
         "updated_at": "2024-10-29T12:34:56Z"
       }
       ```

2. **Remove a Product**

   - **Endpoint**: `DELETE /products/{productId}`
   - **Description**: Permanently remove a product from the inventory (e.g., damaged).
   - **Parameters**:
     - `productId` (Path Parameter): UUID of the product.
   - **Request Body**:

     ```json
     {
       "reason": "Damaged beyond repair"
     }
     ```

   - **Response**:

     - **Status**: `200 OK`
     - **Body**:

       ```json
       {
         "message": "Product removed successfully.",
         "productId": "uuid",
         "reason": "Damaged beyond repair"
       }
       ```

3. **Get Products for Rent**

   - **Endpoint**: `GET /products/available`
   - **Description**: Retrieve all products available for rent.
   - **Response**:

     - **Status**: `200 OK`
     - **Body**:

       ```json
       [
         {
           "id": "uuid",
           "name": "Mountain Bike",
           "productType": "Bike-MEDIUM",
           "basePrice": 15.00,
           "isAvailable": true,
           "description": "A sturdy mountain bike.",
           "lastMaintenanceDate": "2024-10-29T12:34:56Z",
           "size": "MEDIUM",
           "type": null,
           "created_at": "2024-10-29T12:34:56Z",
           "updated_at": "2024-10-29T12:34:56Z"
         },
         {
           "id": "uuid",
           "name": "Electric Scooter",
           "productType": "Scooter-ELECTRIC",
           "basePrice": 20.00,
           "isAvailable": true,
           "description": "A fast electric scooter.",
           "lastMaintenanceDate": "2024-10-29T12:34:56Z",
           "size": null,
           "type": "ELECTRIC",
           "created_at": "2024-10-29T12:34:56Z",
           "updated_at": "2024-10-29T12:34:56Z"
         }
       ]
       ```

4. **Get Available Products by Type**

   - **Endpoint**: `GET /products/available/{type}`
   - **Description**: Retrieve available products filtered by type (e.g., Bike, Scooter-ELECTRIC).
   - **Parameters**:
     - `type` (Path Parameter): String indicating product type.
   - **Response**:

     - **Status**: `200 OK`
     - **Body**:

       ```json
       [
         {
           "id": "uuid",
           "name": "Mountain Bike",
           "productType": "Bike-MEDIUM",
           "basePrice": 15.00,
           "isAvailable": true,
           "description": "A sturdy mountain bike.",
           "lastMaintenanceDate": "2024-10-29T12:34:56Z",
           "size": "MEDIUM",
           "type": null,
           "created_at": "2024-10-29T12:34:56Z",
           "updated_at": "2024-10-29T12:34:56Z"
         }
       ]
       ```

5. **Get Available Products by Price Range**

   - **Endpoint**: `GET /products/available/price-range`
   - **Description**: Retrieve available products within a specified price range.
   - **Query Parameters**:
     - `min` (Double): Minimum price.
     - `max` (Double): Maximum price.
   - **Example Request**: `GET /products/available/price-range?min=10&max=20`
   - **Response**:

     - **Status**: `200 OK`
     - **Body**:

       ```json
       [
         {
           "id": "uuid",
           "name": "Mountain Bike",
           "productType": "Bike-MEDIUM",
           "basePrice": 15.00,
           "isAvailable": true,
           "description": "A sturdy mountain bike.",
           "lastMaintenanceDate": "2024-10-29T12:34:56Z",
           "size": "MEDIUM",
           "type": null,
           "created_at": "2024-10-29T12:34:56Z",
           "updated_at": "2024-10-29T12:34:56Z"
         },
         {
           "id": "uuid",
           "name": "Electric Scooter",
           "productType": "Scooter-ELECTRIC",
           "basePrice": 20.00,
           "isAvailable": true,
           "description": "A fast electric scooter.",
           "lastMaintenanceDate": "2024-10-29T12:34:56Z",
           "size": null,
           "type": "ELECTRIC",
           "created_at": "2024-10-29T12:34:56Z",
           "updated_at": "2024-10-29T12:34:56Z"
         }
       ]
       ```

#### **2.3.3. Rental Endpoints**

1. **Record a Rental**

   - **Endpoint**: `POST /rentals`
   - **Description**: Record that a product is rented to a customer.
   - **Request Body**:

     ```json
     {
       "productId": "uuid",
       "customerId": "uuid",
       "days": 7
     }
     ```

   - **Response**:

     - **Status**: `201 Created`
     - **Body**:

       ```json
       {
         "rentalId": "uuid",
         "productId": "uuid",
         "customerId": "uuid",
         "charge": 135.00, // Example calculated charge
         "rentedAt": "2024-10-29T12:34:56Z",
         "dueDate": "2024-11-05T12:34:56Z"
       }
       ```

2. **Get All Rented Products**

   - **Endpoint**: `GET /rentals/active`
   - **Description**: Retrieve all currently rented products.
   - **Response**:

     - **Status**: `200 OK`
     - **Body**:

       ```json
       [
         {
           "rentalId": "uuid",
           "productId": "uuid",
           "productName": "Mountain Bike",
           "productType": "Bike-MEDIUM",
           "customerId": "uuid",
           "customerName": "John Doe",
           "rentedAt": "2024-10-20T10:00:00Z",
           "dueDate": "2024-10-27T10:00:00Z",
           "isOverdue": false
         }
       ]
       ```

3. **Get Overdue Rentals**

   - **Endpoint**: `GET /rentals/overdue`
   - **Description**: Retrieve all overdue rentals with customer contact information.
   - **Response**:

     - **Status**: `200 OK`
     - **Body**:

       ```json
       [
         {
           "rentalId": "uuid",
           "productId": "uuid",
           "productName": "Electric Scooter",
           "productType": "Scooter-ELECTRIC",
           "customerId": "uuid",
           "customerName": "Jane Smith",
           "customerEmail": "jane.smith@example.com",
           "customerPhone": "098-765-4321",
           "rentedAt": "2024-09-15T09:30:00Z",
           "dueDate": "2024-09-22T09:30:00Z",
           "isOverdue": true,
           "overdueDuration": "7 days",
           "overdueStatus": "SEVERELY_OVERDUE"
         }
       ]
       ```

4. **Get Current Rentals Report**

   - **Endpoint**: `GET /reports/rentals`
   - **Description**: Generate a comprehensive report of current rentals.
   - **Response**:

     - **Status**: `200 OK`
     - **Body**:

       ```json
       {
         "title": "Current Rental Report",
         "summary": {
           "Bike-MEDIUM": 5,
           "Scooter-ELECTRIC": 3
         },
         "data": [
           {
             "rentalId": "uuid",
             "productId": "uuid",
             "productName": "Mountain Bike",
             "productType": "Bike-MEDIUM",
             "customerId": "uuid",
             "customerName": "John Doe",
             "rentedAt": "2024-10-20T10:00:00Z",
             "dueDate": "2024-10-27T10:00:00Z"
           },
           // More RentalInfo objects
         ]
       }
       ```

5. **Get Overdue Rentals Report**

   - **Endpoint**: `GET /reports/rentals/overdue`
   - **Description**: Generate a report of all overdue rentals.
   - **Response**:

     - **Status**: `200 OK`
     - **Body**:

       ```json
       {
         "title": "Overdue Rentals Report",
         "summary": {
           "SEVERELY_OVERDUE": 2,
           "MODERATELY_OVERDUE": 1,
           "SLIGHTLY_OVERDUE": 3
         },
         "data": [
           {
             "rentalId": "uuid",
             "productId": "uuid",
             "productName": "Electric Scooter",
             "productType": "Scooter-ELECTRIC",
             "customerId": "uuid",
             "customerName": "Jane Smith",
             "customerEmail": "jane.smith@example.com",
             "customerPhone": "098-765-4321",
             "rentedAt": "2024-09-15T09:30:00Z",
             "dueDate": "2024-09-22T09:30:00Z",
             "isOverdue": true,
             "overdueDuration": "7 days",
             "overdueStatus": "SEVERELY_OVERDUE"
           },
           // More RentalInfo objects
         ]
       }
       ```

#### **2.3.4. Inventory Endpoints**

1. **Get Number of Bikes by Size**

   - **Endpoint**: `GET /inventory/bikes/{size}/count`
   - **Description**: Retrieve the count of bikes by size.
   - **Parameters**:
     - `size` (Path Parameter): Enum (`SMALL`, `MEDIUM`, `LARGE`)
   - **Response**:

     - **Status**: `200 OK`
     - **Body**:

       ```json
       {
         "size": "SMALL",
         "totalCount": 10,
         "availableCount": 7,
         "rentedCount": 3
       }
       ```

2. **Get Full Bike Inventory Report**

   - **Endpoint**: `GET /inventory/bikes/report`
   - **Description**: Retrieve a comprehensive report of bike inventory.
   - **Response**:

     - **Status**: `200 OK`
     - **Body**:

       ```json
       {
         "title": "Bike Inventory Report",
         "summary": {
           "SMALL Bikes": 10,
           "MEDIUM Bikes": 15,
           "LARGE Bikes": 5
         },
         "data": [
           {
             "size": "SMALL",
             "totalCount": 10,
             "availableCount": 7
           },
           {
             "size": "MEDIUM",
             "totalCount": 15,
             "availableCount": 12
           },
           {
             "size": "LARGE",
             "totalCount": 5,
             "availableCount": 4
           }
         ]
       }
       ```

#### **2.3.5. Pricing Endpoint**

1. **Calculate Rental Price**

   - **Endpoint**: `GET /pricing/calculate`
   - **Description**: Calculate the rental price for a product based on rental duration.
   - **Query Parameters**:
     - `productId` (UUID): ID of the product.
     - `days` (Integer): Number of rental days.
   - **Response**:

     - **Status**: `200 OK`
     - **Body**:

       ```json
       {
         "productId": "uuid",
         "days": 7,
         "basePrice": 15.00,
         "discount": 0.10,
         "seasonalMultiplier": 1.2,
         "finalPrice": 16.20
       }
       ```

     **Explanation**:

     - **basePrice**: Product's base price multiplied by the number of days.
     - **discount**: Applicable discount percentage.
     - **seasonalMultiplier**: Seasonal multiplier based on the current month.
     - **finalPrice**: Calculated price after applying discount and seasonal multiplier.

---

## **3. Detailed API Design**

Below is a more detailed specification of each endpoint, including HTTP methods, URIs, request/response bodies, and status codes.

### **3.1. Customer Endpoints**

#### **3.1.1. Add a Customer**

- **Method**: `POST`
- **URI**: `/customers`
- **Description**: Adds a new customer to the system.
- **Request Headers**:
  - `Content-Type: application/json`
- **Request Body**:

  ```json
  {
    "name": "John Doe",
    "size": "MEDIUM",
    "email": "john.doe@example.com",
    "phone": "123-456-7890"
  }
  ```

- **Response**:
  - **Status**: `201 Created`
  - **Headers**:
    - `Location: /customers/{customerId}`
  - **Body**:

    ```json
    {
      "id": "uuid",
      "name": "John Doe",
      "size": "MEDIUM",
      "balance": 0.00,
      "email": "john.doe@example.com",
      "phone": "123-456-7890",
      "created_at": "2024-10-29T12:34:56Z",
      "updated_at": "2024-10-29T12:34:56Z"
    }
    ```

#### **3.1.2. Get Customer Balance**

- **Method**: `GET`
- **URI**: `/customers/{customerId}/balance`
- **Description**: Retrieves the balance owed by a customer.
- **Response**:
  - **Status**: `200 OK`
  - **Body**:

    ```json
    {
      "customerId": "uuid",
      "balance": 25.50
    }
    ```

- **Error Responses**:
  - **Status**: `404 Not Found`
  - **Body**:

    ```json
    {
      "error": "CustomerNotFoundException",
      "message": "Customer not found: {customerId}"
    }
    ```

#### **3.1.3. Get Customer Rentals**

- **Method**: `GET`
- **URI**: `/customers/{customerId}/rentals`
- **Description**: Retrieves all rentals associated with a customer.
- **Response**:
  - **Status**: `200 OK`
  - **Body**:

    ```json
    [
      {
        "rentalId": "uuid",
        "productId": "uuid",
        "productName": "Mountain Bike",
        "productType": "Bike-MEDIUM",
        "rentedAt": "2024-10-20T10:00:00Z",
        "dueDate": "2024-10-27T10:00:00Z",
        "isOverdue": false
      },
      {
        "rentalId": "uuid",
        "productId": "uuid",
        "productName": "Electric Scooter",
        "productType": "Scooter-ELECTRIC",
        "rentedAt": "2024-09-15T09:30:00Z",
        "dueDate": "2024-09-22T09:30:00Z",
        "isOverdue": true
      }
    ]
    ```

- **Error Responses**:
  - **Status**: `404 Not Found`
  - **Body**:

    ```json
    {
      "error": "CustomerNotFoundException",
      "message": "Customer not found: {customerId}"
    }
    ```

### **3.2. Product Endpoints**

#### **3.2.1. Add a Product**

- **Method**: `POST`
- **URI**: `/products`
- **Description**: Adds a new product (Bike or Scooter) to the inventory.
- **Request Headers**:
  - `Content-Type: application/json`
- **Request Body**:

  - **For Bikes**:

    ```json
    {
      "name": "Mountain Bike",
      "productType": "Bike",
      "basePrice": 15.00,
      "description": "A sturdy mountain bike.",
      "size": "MEDIUM"
    }
    ```

  - **For Scooters**:

    ```json
    {
      "name": "Electric Scooter",
      "productType": "Scooter",
      "basePrice": 20.00,
      "description": "A fast electric scooter.",
      "type": "ELECTRIC"
    }
    ```

- **Response**:
  - **Status**: `201 Created`
  - **Headers**:
    - `Location: /products/{productId}`
  - **Body**:

    ```json
    {
      "id": "uuid",
      "name": "Mountain Bike",
      "productType": "Bike-MEDIUM",
      "basePrice": 15.00,
      "isAvailable": true,
      "description": "A sturdy mountain bike.",
      "lastMaintenanceDate": "2024-10-29T12:34:56Z",
      "size": "MEDIUM",
      "type": null,
      "created_at": "2024-10-29T12:34:56Z",
      "updated_at": "2024-10-29T12:34:56Z"
    }
    ```

- **Error Responses**:
  - **Status**: `400 Bad Request`
  - **Body**:

    ```json
    {
      "error": "InvalidProductException",
      "message": "Product price must be positive"
    }
    ```

#### **3.2.2. Remove a Product**

- **Method**: `DELETE`
- **URI**: `/products/{productId}`
- **Description**: Permanently removes a product from the inventory (e.g., damaged).
- **Request Headers**:
  - `Content-Type: application/json`
- **Request Body**:

  ```json
  {
    "reason": "Damaged beyond repair"
  }
  ```

- **Response**:
  - **Status**: `200 OK`
  - **Body**:

    ```json
    {
      "message": "Product removed successfully.",
      "productId": "uuid",
      "reason": "Damaged beyond repair"
    }
    ```

- **Error Responses**:
  - **Status**: `404 Not Found`
  - **Body**:

    ```json
    {
      "error": "ProductNotFoundException",
      "message": "Product not found: {productId}"
    }
    ```

  - **Status**: `409 Conflict`
  - **Body**:

    ```json
    {
      "error": "ProductInUseException",
      "message": "Product is currently rented"
    }
    ```

#### **3.2.3. Get Products for Rent**

- **Method**: `GET`
- **URI**: `/products/available`
- **Description**: Retrieves all products currently available for rent.
- **Response**:
  - **Status**: `200 OK`
  - **Body**:

    ```json
    [
      {
        "id": "uuid",
        "name": "Mountain Bike",
        "productType": "Bike-MEDIUM",
        "basePrice": 15.00,
        "isAvailable": true,
        "description": "A sturdy mountain bike.",
        "lastMaintenanceDate": "2024-10-29T12:34:56Z",
        "size": "MEDIUM",
        "type": null,
        "created_at": "2024-10-29T12:34:56Z",
        "updated_at": "2024-10-29T12:34:56Z"
      },
      // More products...
    ]
    ```

#### **3.2.4. Get Available Products by Type**

- **Method**: `GET`
- **URI**: `/products/available/{type}`
- **Description**: Retrieves available products filtered by type (e.g., Bike, Scooter-ELECTRIC).
- **Parameters**:
  - `type` (Path Parameter): String indicating product type.
- **Response**:
  - **Status**: `200 OK`
  - **Body**:

    ```json
    [
      {
        "id": "uuid",
        "name": "Mountain Bike",
        "productType": "Bike-MEDIUM",
        "basePrice": 15.00,
        "isAvailable": true,
        "description": "A sturdy mountain bike.",
        "lastMaintenanceDate": "2024-10-29T12:34:56Z",
        "size": "MEDIUM",
        "type": null,
        "created_at": "2024-10-29T12:34:56Z",
        "updated_at": "2024-10-29T12:34:56Z"
      }
      // More products...
    ]
    ```

#### **3.2.5. Get Available Products by Price Range**

- **Method**: `GET`
- **URI**: `/products/available/price-range`
- **Description**: Retrieves available products within a specified price range.
- **Query Parameters**:
  - `min` (Double): Minimum price.
  - `max` (Double): Maximum price.
- **Example Request**: `GET /products/available/price-range?min=10&max=20`
- **Response**:
  - **Status**: `200 OK`
  - **Body**:

    ```json
    [
      {
        "id": "uuid",
        "name": "Mountain Bike",
        "productType": "Bike-MEDIUM",
        "basePrice": 15.00,
        "isAvailable": true,
        "description": "A sturdy mountain bike.",
        "lastMaintenanceDate": "2024-10-29T12:34:56Z",
        "size": "MEDIUM",
        "type": null,
        "created_at": "2024-10-29T12:34:56Z",
        "updated_at": "2024-10-29T12:34:56Z"
      },
      {
        "id": "uuid",
        "name": "Electric Scooter",
        "productType": "Scooter-ELECTRIC",
        "basePrice": 20.00,
        "isAvailable": true,
        "description": "A fast electric scooter.",
        "lastMaintenanceDate": "2024-10-29T12:34:56Z",
        "size": null,
        "type": "ELECTRIC",
        "created_at": "2024-10-29T12:34:56Z",
        "updated_at": "2024-10-29T12:34:56Z"
      }
      // More products...
    ]
    ```

### **3.3. Rental Endpoints**

#### **3.3.1. Record a Rental**

- **Method**: `POST`
- **URI**: `/rentals`
- **Description**: Records that a product is rented to a customer.
- **Request Headers**:
  - `Content-Type: application/json`
- **Request Body**:

  ```json
  {
    "productId": "uuid",
    "customerId": "uuid",
    "days": 7
  }
  ```

- **Response**:
  - **Status**: `201 Created`
  - **Headers**:
    - `Location: /rentals/{rentalId}`
  - **Body**:

    ```json
    {
      "rentalId": "uuid",
      "productId": "uuid",
      "customerId": "uuid",
      "charge": 135.00, // Example calculated charge
      "rentedAt": "2024-10-29T12:34:56Z",
      "dueDate": "2024-11-05T12:34:56Z"
    }
    ```

- **Error Responses**:
  - **Status**: `400 Bad Request`
  - **Body**:

    ```json
    {
      "error": "InvalidRentalDurationException",
      "message": "Rental duration must be positive"
    }
    ```

  - **Status**: `404 Not Found`
  - **Body**:

    ```json
    {
      "error": "ProductNotFoundException",
      "message": "Product not found: {productId}"
    }
    ```

  - **Status**: `409 Conflict`
  - **Body**:

    ```json
    {
      "error": "ProductNotAvailableException",
      "message": "Product is not available for rent"
    }
    ```

#### **3.3.2. Get All Rented Products**

- **Method**: `GET`
- **URI**: `/rentals/active`
- **Description**: Retrieves all currently rented products.
- **Response**:
  - **Status**: `200 OK`
  - **Body**:

    ```json
    [
      {
        "rentalId": "uuid",
        "productId": "uuid",
        "productName": "Mountain Bike",
        "productType": "Bike-MEDIUM",
        "customerId": "uuid",
        "customerName": "John Doe",
        "rentedAt": "2024-10-20T10:00:00Z",
        "dueDate": "2024-10-27T10:00:00Z",
        "isOverdue": false
      }
      // More rentals...
    ]
    ```

#### **3.3.3. Get Overdue Rentals**

- **Method**: `GET`
- **URI**: `/rentals/overdue`
- **Description**: Retrieves all overdue rentals with customer contact information.
- **Response**:
  - **Status**: `200 OK`
  - **Body**:

    ```json
    [
      {
        "rentalId": "uuid",
        "productId": "uuid",
        "productName": "Electric Scooter",
        "productType": "Scooter-ELECTRIC",
        "customerId": "uuid",
        "customerName": "Jane Smith",
        "customerEmail": "jane.smith@example.com",
        "customerPhone": "098-765-4321",
        "rentedAt": "2024-09-15T09:30:00Z",
        "dueDate": "2024-09-22T09:30:00Z",
        "isOverdue": true,
        "overdueDuration": "7 days",
        "overdueStatus": "SEVERELY_OVERDUE"
      }
      // More overdue rentals...
    ]
    ```

#### **3.3.4. Get Current Rentals Report**

- **Method**: `GET`
- **URI**: `/reports/rentals`
- **Description**: Generates a comprehensive report of current rentals.
- **Response**:
  - **Status**: `200 OK`
  - **Body**:

    ```json
    {
      "title": "Current Rental Report",
      "summary": {
        "Bike-MEDIUM": 5,
        "Scooter-ELECTRIC": 3
      },
      "data": [
        {
          "rentalId": "uuid",
          "productId": "uuid",
          "productName": "Mountain Bike",
          "productType": "Bike-MEDIUM",
          "customerId": "uuid",
          "customerName": "John Doe",
          "rentedAt": "2024-10-20T10:00:00Z",
          "dueDate": "2024-10-27T10:00:00Z"
        }
        // More RentalInfo objects...
      ]
    }
    ```

#### **3.3.5. Get Overdue Rentals Report**

- **Method**: `GET`
- **URI**: `/reports/rentals/overdue`
- **Description**: Generates a report of all overdue rentals.
- **Response**:
  - **Status**: `200 OK`
  - **Body**:

    ```json
    {
      "title": "Overdue Rentals Report",
      "summary": {
        "SEVERELY_OVERDUE": 2,
        "MODERATELY_OVERDUE": 1,
        "SLIGHTLY_OVERDUE": 3
      },
      "data": [
        {
          "rentalId": "uuid",
          "productId": "uuid",
          "productName": "Electric Scooter",
          "productType": "Scooter-ELECTRIC",
          "customerId": "uuid",
          "customerName": "Jane Smith",
          "customerEmail": "jane.smith@example.com",
          "customerPhone": "098-765-4321",
          "rentedAt": "2024-09-15T09:30:00Z",
          "dueDate": "2024-09-22T09:30:00Z",
          "isOverdue": true,
          "overdueDuration": "7 days",
          "overdueStatus": "SEVERELY_OVERDUE"
        }
        // More Overdue RentalInfo objects...
      ]
    }
    ```

### **3.4. Inventory Endpoints**

#### **3.4.1. Get Number of Bikes by Size**

- **Method**: `GET`
- **URI**: `/inventory/bikes/{size}/count`
- **Description**: Retrieves the count of bikes based on size.
- **Parameters**:
  - `size` (Path Parameter): Enum (`SMALL`, `MEDIUM`, `LARGE`)
- **Response**:
  - **Status**: `200 OK`
  - **Body**:

    ```json
    {
      "size": "SMALL",
      "totalCount": 10,
      "availableCount": 7,
      "rentedCount": 3
    }
    ```

#### **3.4.2. Get Full Bike Inventory Report**

- **Method**: `GET`
- **URI**: `/inventory/bikes/report`
- **Description**: Retrieves a comprehensive report of all bike inventories.
- **Response**:
  - **Status**: `200 OK`
  - **Body**:

    ```json
    {
      "title": "Bike Inventory Report",
      "summary": {
        "SMALL Bikes": 10,
        "MEDIUM Bikes": 15,
        "LARGE Bikes": 5
      },
      "data": [
        {
          "size": "SMALL",
          "totalCount": 10,
          "availableCount": 7
        },
        {
          "size": "MEDIUM",
          "totalCount": 15,
          "availableCount": 12
        },
        {
          "size": "LARGE",
          "totalCount": 5,
          "availableCount": 4
        }
      ]
    }
    ```

### **3.5. Pricing Endpoint**

#### **3.5.1. Calculate Rental Price**

- **Method**: `GET`
- **URI**: `/pricing/calculate`
- **Description**: Calculates the rental price for a product based on rental duration.
- **Query Parameters**:
  - `productId` (UUID): ID of the product.
  - `days` (Integer): Number of rental days.
- **Example Request**: `GET /pricing/calculate?productId=uuid&days=7`
- **Response**:
  - **Status**: `200 OK`
  - **Body**:

    ```json
    {
      "productId": "uuid",
      "days": 7,
      "basePrice": 15.00,
      "discount": 0.10,
      "seasonalMultiplier": 1.2,
      "finalPrice": 16.20
    }
    ```

- **Error Responses**:
  - **Status**: `400 Bad Request`
  - **Body**:

    ```json
    {
      "error": "InvalidRentalDurationException",
      "message": "Rental duration must be positive"
    }
    ```

  - **Status**: `404 Not Found`
  - **Body**:

    ```json
    {
      "error": "ProductNotFoundException",
      "message": "Product not found: {productId}"
    }
    ```

### **3.6. Charge Endpoint**

1. **Create a Charge for a Customer**

   - **Method**: `POST`
   - **URI**: `/charges`
   - **Description**: Creates a charge for a customer.
   - **Request Headers**:
     - `Content-Type: application/json`
   - **Request Body**:

     ```json
     {
       "customerId": "uuid",
       "amount": 25.50,
       "description": "Late return fee"
     }
     ```

   - **Response**:
     - **Status**: `201 Created`
     - **Headers**:
       - `Location: /charges/{chargeId}`
     - **Body**:

       ```json
       {
         "id": "uuid",
         "customerId": "uuid",
         "amount": 25.50,
         "description": "Late return fee",
         "chargedAt": "2024-10-29T12:34:56Z",
         "created_at": "2024-10-29T12:34:56Z",
         "updated_at": "2024-10-29T12:34:56Z"
       }
       ```

   - **Error Responses**:
     - **Status**: `400 Bad Request`
     - **Body**:

       ```json
       {
         "error": "InvalidChargeException",
         "message": "Charge amount must be positive"
       }
       ```

     - **Status**: `404 Not Found`
     - **Body**:

       ```json
       {
         "error": "CustomerNotFoundException",
         "message": "Customer not found: {customerId}"
       }
       ```

---

## **4. Handling Variable Pricing**

### **4.1. Discussion**

Variable pricing is essential to adapt to different rental durations, seasons, and product-specific attributes. Here's how the system handles it:

1. **Rental Duration Discounts**:
   - **1 Day**: 0% discount.
   - **1 Week (7 Days)**: 10% discount.
   - **2 Weeks (14 Days)**: 15% discount.
   - **1 Month (30 Days)**: 25% discount.

2. **Seasonal Multipliers**:
   - **June**: 1.2x
   - **July & August**: 1.3x
   - **December**: 1.2x
   - **Other Months**: 1.0x

3. **Bike Size Premiums**:
   - **Small**: No premium.
   - **Medium**: 1.1x
   - **Large**: 1.2x

4. **Promotions**:
   - Additional promotions can be integrated into the pricing strategy as needed.

### **4.2. Implementation in `UnifiedPricingStrategy`**

The `UnifiedPricingStrategy` class integrates all these aspects:

1. **Apply Highest Applicable Discount** based on rental duration.
2. **Apply Seasonal Multiplier** based on the current month.
3. **Apply Bike Size Premium** if the product is a bike.

### **4.3. Example Calculation**

- **Product**: Mountain Bike
- **Base Price**: $15.00/day
- **Rental Duration**: 7 days (1 week)
- **Season**: July
- **Bike Size**: MEDIUM

**Calculation Steps**:

1. **Base Price**: $15.00 * 7 = $105.00
2. **Discount**: 10% (for 7 days) → $105.00 * 0.90 = $94.50
3. **Seasonal Multiplier**: 1.3 (July) → $94.50 * 1.3 = $122.85
4. **Bike Size Premium**: 1.1 (MEDIUM) → $122.85 * 1.1 = $135.135 ≈ $135.14

**Final Price**: $135.14

### **4.4. API Endpoint for Price Calculation**

The previously defined `GET /pricing/calculate` endpoint facilitates this calculation by taking `productId` and `days` as inputs and returning the detailed pricing breakdown.

---

## **5. Additional Considerations**

### **5.1. Authentication & Authorization**

Implement secure authentication (e.g., JWT) and role-based access control to ensure that only authorized personnel can perform certain operations like adding/removing products or accessing customer balances.

### **5.2. Validation**

Use server-side validation for all inputs to ensure data integrity and prevent malformed requests.

### **5.3. Error Handling**

Implement a global exception handler using `@ControllerAdvice` in Spring Boot to uniformly manage and format error responses.

### **5.4. Pagination and Filtering**

For endpoints returning lists (e.g., available products, rentals), consider implementing pagination and additional filtering options to handle large datasets efficiently.

### **5.5. Documentation**

Utilize tools like **Swagger/OpenAPI** to document the API endpoints, making it easier for clients to understand and interact with the API.

---

## **6. Sample Code Snippets**

Below are sample implementations for some of the designed API endpoints using **Spring Boot**.

### **6.1. Customer Controller**

```java
package com.rental.controller;

import com.rental.domain.Customer;
import com.rental.dto.CustomerBalance;
import com.rental.dto.CustomerResponse;
import com.rental.service.RentalService;
import com.rental.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    private final CustomerService customerService;
    private final RentalService rentalService;

    public CustomerController(CustomerService customerService, RentalService rentalService) {
        this.customerService = customerService;
        this.rentalService = rentalService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> addCustomer(@RequestBody CustomerRequest request) {
        Customer customer = customerService.addCustomer(request);
        CustomerResponse response = CustomerResponse.fromCustomer(customer);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{customerId}/balance")
    public ResponseEntity<CustomerBalance> getCustomerBalance(@PathVariable String customerId) {
        double balance = rentalService.getCustomerBalance(customerId);
        CustomerBalance response = new CustomerBalance(customerId, balance);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{customerId}/rentals")
    public ResponseEntity<List<RentalInfo>> getCustomerRentals(@PathVariable String customerId) {
        List<RentalInfo> rentals = rentalService.getCustomerRentals(customerId);
        return ResponseEntity.ok(rentals);
    }

    // Additional endpoints as needed
}
```

### **6.2. Product Controller**

```java
package com.rental.controller;

import com.rental.domain.Product;
import com.rental.dto.ProductResponse;
import com.rental.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @PostMapping
    public ResponseEntity<ProductResponse> addProduct(@RequestBody ProductRequest request) {
        Product product = productService.addProduct(request);
        ProductResponse response = ProductResponse.fromProduct(product);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<RemoveProductResponse> removeProduct(
            @PathVariable String productId,
            @RequestBody RemoveProductRequest request) {
        productService.removeProduct(productId, request.getReason());
        RemoveProductResponse response = new RemoveProductResponse("Product removed successfully.", productId, request.getReason());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/available")
    public ResponseEntity<List<ProductResponse>> getAvailableProducts() {
        List<Product> products = productService.getAvailableProducts();
        List<ProductResponse> responses = products.stream()
                .map(ProductResponse::fromProduct)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/available/{type}")
    public ResponseEntity<List<ProductResponse>> getAvailableProductsByType(@PathVariable String type) {
        List<Product> products = productService.getAvailableProductsByType(type);
        List<ProductResponse> responses = products.stream()
                .map(ProductResponse::fromProduct)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/available/price-range")
    public ResponseEntity<List<ProductResponse>> getAvailableProductsByPriceRange(
            @RequestParam double min,
            @RequestParam double max) {
        List<Product> products = productService.getAvailableProductsByPriceRange(min, max);
        List<ProductResponse> responses = products.stream()
                .map(ProductResponse::fromProduct)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // Additional endpoints as needed
}
```

### **6.3. Rental Controller**

```java
package com.rental.controller;

import com.rental.dto.RentalReceipt;
import com.rental.dto.RentalInfo;
import com.rental.service.RentalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rentals")
public class RentalController {
    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @PostMapping
    public ResponseEntity<RentalReceipt> rentProduct(@RequestBody RentProductRequest request) {
        RentalReceipt receipt = rentalService.rentProduct(request.getProductId(), request.getCustomerId(), request.getDays());
        return new ResponseEntity<>(receipt, HttpStatus.CREATED);
    }

    @GetMapping("/active")
    public ResponseEntity<List<RentalInfo>> getActiveRentals() {
        List<RentalInfo> rentals = rentalService.getActiveRentals();
        return ResponseEntity.ok(rentals);
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<RentalInfo>> getOverdueRentals() {
        List<RentalInfo> overdueRentals = rentalService.getOverdueRentals();
        return ResponseEntity.ok(overdueRentals);
    }

    // Additional endpoints as needed
}
```

### **6.4. Pricing Controller**

```java
package com.rental.controller;

import com.rental.dto.PricingResponse;
import com.rental.service.PricingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pricing")
public class PricingController {
    private final PricingService pricingService;

    public PricingController(PricingService pricingService) {
        this.pricingService = pricingService;
    }

    @GetMapping("/calculate")
    public ResponseEntity<PricingResponse> calculateRentalPrice(
            @RequestParam String productId,
            @RequestParam int days) {
        PricingResponse response = pricingService.calculatePrice(productId, days);
        return ResponseEntity.ok(response);
    }

    // Additional endpoints as needed
}
```

---

## **7. Summary**

The **Database Schema** and **API Design** provided above are meticulously crafted to align with the Bike Rental Shop's requirements and the refactored code structure. Here's a recap of what has been achieved:

1. **Database Schema Design**:
   - **Entities**: Defined `Customer`, `Product` (with `Bike` and `Scooter` subtypes), `Rental`, and `Charge`.
   - **Relationships**: Established foreign key relationships ensuring data integrity.
   - **Inheritance Strategy**: Utilized Single Table Inheritance for simplicity and performance.
   - **Indexes**: Added indexes on frequently queried fields to optimize performance.

2. **API Design**:
   - **RESTful Principles**: Adhered to standard REST conventions using appropriate HTTP methods and status codes.
   - **Endpoints**: Created endpoints covering all required queries and commands, including customer management, product inventory, rentals, charges, and pricing calculations.
   - **Request/Response Models**: Defined clear and concise request and response payloads for each endpoint.
   - **Error Handling**: Structured error responses to provide meaningful feedback to API consumers.

3. **Variable Pricing Handling**:
   - **Integrated Discounts, Seasonal Multipliers, and Bike Size Premiums** into a unified pricing strategy.
   - **API Endpoint**: Provided an endpoint to calculate rental prices dynamically based on various factors.

4. **Additional Enhancements**:
   - **Authentication & Authorization**: Recommended securing the API to protect sensitive operations.
   - **Validation & Error Handling**: Emphasized the importance of input validation and consistent error responses.
   - **Documentation**: Suggested using Swagger/OpenAPI for comprehensive API documentation.

---

## **8. Next Steps**

To proceed with the implementation:

1. **Database Setup**:
   - Use a relational database like **PostgreSQL** or **MySQL**.
   - Apply the table definitions using SQL scripts or a migration tool like **Flyway** or **Liquibase**.

2. **API Development**:
   - Implement the controllers, services, and repositories as per the API design.
   - Utilize **Spring Boot** for rapid development and ease of integration.
   - Ensure to handle exceptions gracefully and validate all inputs.

3. **Testing**:
   - Write unit tests for services and controllers.
   - Perform integration testing to ensure end-to-end functionality.
   - Use tools like **Postman** or **Swagger UI** to manually test API endpoints.

4. **Documentation**:
   - Generate API documentation using **Swagger/OpenAPI** annotations.
   - Ensure the documentation is up-to-date with the API implementations.

5. **Security**:
   - Implement authentication (e.g., JWT) and authorization to secure API endpoints.
   - Ensure sensitive data like customer balances are protected.

6. **Deployment**:
   - Prepare the application for deployment using containerization tools like **Docker**.
   - Set up CI/CD pipelines for automated testing and deployment.

---

If you need further assistance with specific aspects of the implementation, such as writing the actual Java code for controllers, services, or configuring the database, feel free to ask!