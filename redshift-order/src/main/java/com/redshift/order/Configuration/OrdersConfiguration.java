package com.redshift.order.Configuration;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("redshift")
public class OrdersConfiguration {

    private String paymentUrl;

    private  String hystrixTimeOut;

    public String getHystrixTimeOut() {
        return hystrixTimeOut;
    }

    public void setHystrixTimeOut( String hystrixTimeOut) {
        this.hystrixTimeOut = hystrixTimeOut;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }
}
