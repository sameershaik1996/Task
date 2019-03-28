package com.redshift.order.Model;

import com.redshift.order.Domain.Orders;

import java.io.Serializable;

public class OrderDto implements Serializable {

    private Orders orders;
    public OrderDto(){

    }
    public OrderDto(Orders orders, PaymentModel paymentModel) {
        this.orders = orders;
        this.paymentModel = paymentModel;
    }

    private PaymentModel paymentModel;

    public Orders getOrders() {
        return orders;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
    }

    public PaymentModel getPaymentModel() {
        return paymentModel;
    }

    public void setPaymentModel(PaymentModel paymentModel) {
        this.paymentModel = paymentModel;
    }
}
