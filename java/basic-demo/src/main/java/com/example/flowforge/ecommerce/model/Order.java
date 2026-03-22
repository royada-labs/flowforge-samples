package com.example.flowforge.ecommerce.model;

public class Order {
    private String id;
    private Double amount;
    private String status;
    private String customerEmail;

    public Order(String id, Double amount, String status, String customerEmail) {
        this.id = id;
        this.amount = amount;
        this.status = status;
        this.customerEmail = customerEmail;
    }

    public String getId() { return id; }
    public Double getAmount() { return amount; }
    public String getStatus() { return status; }
    public String getCustomerEmail() { return customerEmail; }

    @Override
    public String toString() {
        return "Order{id='" + id + "', amount=" + amount + ", status='" + status + "'}";
    }
}
