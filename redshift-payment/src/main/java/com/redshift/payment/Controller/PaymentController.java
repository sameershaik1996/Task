package com.redshift.payment.Controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.redshift.payment.Data.PaymentRepository;
import com.redshift.payment.Domain.Payment;
import com.redshift.payment.RedshiftExceptionHandling;
import com.redshift.payment.Response.RedshiftResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    PaymentRepository _paymentRepository;



    @RequestMapping(value = "", method = RequestMethod.POST)
    public Payment CreatePayment(@Valid @RequestBody Payment payment) {
        try {
            Thread.sleep(5000);
        }catch (Exception e){}
        System.out.println(payment.getOrder_id());
if(payment.getCardType().equalsIgnoreCase("visa"))
        return _paymentRepository.save(payment);
else
    return new Payment();
    }

    @RequestMapping(value="{id}",method= RequestMethod.GET)
    public Payment GetPayment(@PathVariable(value="id") Long id){

        System.out.println("hi");
        return _paymentRepository.findById(id).orElseThrow(()->new RedshiftExceptionHandling("not found","id",id));
    }



    @RequestMapping(value="/orders/{orderId}",method= RequestMethod.GET)
    public Payment GetPaymentByOrderId(@PathVariable(value="orderId") Long id){

        return _paymentRepository.getPaymentByOrderId(id);
        //return _paymentRepository.findById(id).orElseThrow(()->new RedshiftExceptionHandling("not found","id",id));
    }


    @RequestMapping(value="{id}",method = RequestMethod.PUT)
    public Payment updatePayment(@PathVariable(value="id") Long id, @Valid @RequestBody Payment payment) {
        Payment current_payment= GetPayment(id);
        current_payment.setCardNumber(payment.getCardNumber());
        current_payment.setCardType(payment.getCardType());
        current_payment.setPaymentHash(payment.getPaymentHash());
        return _paymentRepository.save(current_payment);
    }

    @RequestMapping(value="/orders/{orderId}",method = RequestMethod.PUT)
    public Payment updatePaymentByOrderId(@PathVariable(value="orderId") Long id, @Valid @RequestBody Payment payment) {
        Payment current_payment= GetPaymentByOrderId(id);
        current_payment.setCardNumber(payment.getCardNumber());
        current_payment.setCardType(payment.getCardType());
        current_payment.setPaymentHash(payment.getPaymentHash());
        return _paymentRepository.save(current_payment);
    }
}
