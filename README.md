Order–Inventory Management System (Event-Driven Microservices)
Overview

This project is an event-driven microservices system built using Spring Boot, Apache Kafka, and MySQL, designed to demonstrate Saga-based distributed transaction management.

The system handles order creation, inventory reservation, and order confirmation using asynchronous communication between services, ensuring loose coupling and scalability.

Microservices Architecture

Currently implemented services:

Order Service

Inventory Service

(Planned) Payment Service

(Planned) Notification Service

Base Module (shared event & DTO definitions)

Communication: All services communicate via Kafka events.

High-Level Flow
1️⃣ Order Creation

Client sends a POST /orders request

Order is stored with status PENDING

ORDER_CREATED event is published to Kafka

2️⃣ Inventory Reservation

Inventory Service consumes ORDER_CREATED events

For each item:

Checks product existence

Checks available quantity

If successful:

Reduces totalQuantity

Increases reservedQuantity

Publishes INVENTORY_RESERVED

If failed:

Publishes INVENTORY_REJECTED

3️⃣ Order Status Update

Order Service consumes inventory events

On INVENTORY_RESERVED:

Order → CONFIRMED

Publishes ORDER_CONFIRMED

On INVENTORY_REJECTED:

Order → CANCELLED

Publishes ORDER_CANCELLED

4️⃣ Inventory Finalization

Inventory Service consumes ORDER_CONFIRMED events

Reduces reservedQuantity

Inventory is finalized

Inventory Quantity Model
Field	Description
totalQuantity	Actual stock count
reservedQuantity	Temporarily reserved stock for pending orders

This prevents over-selling and supports asynchronous processing.

Kafka Events Used

Produced Events:

ORDER_CREATED

INVENTORY_RESERVED

INVENTORY_REJECTED

ORDER_CONFIRMED

ORDER_CANCELLED

Event Structure (Example):

{
  "baseEvent": {
    "eventId": "uuid",
    "eventType": "ORDER_CONFIRMED"
  },
  "orderId": "uuid",
  "items": [
    { "productId": "P01", "quantity": 1 }
  ]
}
Future Enhancements

Payment Service – consume INVENTORY_RESERVED events for payment processing

Notification Service – send email notifications on order status changes
