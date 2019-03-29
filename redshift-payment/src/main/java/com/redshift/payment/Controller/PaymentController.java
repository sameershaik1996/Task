package com.redshift.payment.Controller;

import com.hazelcast.core.HazelcastInstance;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.redshift.payment.Data.PaymentRepository;
import com.redshift.payment.Domain.Payment;
import com.redshift.payment.RedshiftExceptionHandling;
import com.redshift.payment.Response.RedshiftResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CacheConfig(cacheNames = "payments")
public class PaymentController {

    @Autowired
    PaymentRepository _paymentRepository;

    @Autowired
    private HazelcastInstance instance;


    @RequestMapping(value = "", method = RequestMethod.POST)
    @Cacheable(key="#payment.getOrderId()")
    public Payment CreatePayment(@Valid @RequestBody Payment payment) {

        System.out.println(payment.getOrder_id());
if(payment.getCardType().equalsIgnoreCase("visa"))
{
    Payment created_payment = _paymentRepository.save(payment);

    Map<String,Payment> stringStringMap = instance.getMap("payments");
    System.out.println(stringStringMap);
    stringStringMap.put("payment-order-"+created_payment.getOrder_id(),created_payment);

    return created_payment;


}
else
    return new Payment();
    }

    @RequestMapping(value="{id}",method= RequestMethod.GET)
    public Payment GetPayment(@PathVariable(value="id") Long id){

        System.out.println("hi");
        Map<String,Payment> stringStringMap = instance.getMap("payments");
        Payment result=stringStringMap.get("payment-order-"+id);

        return _paymentRepository.findById(id).orElseThrow(()->new RedshiftExceptionHandling("not found","id",id));
    }


    @Cacheable(key="#id")
    @RequestMapping(value="/orders/{orderId}",method= RequestMethod.GET)
    public Payment GetPaymentByOrderId(@PathVariable(value="orderId") Long id){
        Map<String,Payment> stringStringMap = instance.getMap("payments");
        System.out.println(stringStringMap);
        Payment result=stringStringMap.get("payment-order-"+id);
        try{
            System.out.println(result.getId());
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        return result!=null?result:_paymentRepository.getPaymentByOrderId(id);
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
