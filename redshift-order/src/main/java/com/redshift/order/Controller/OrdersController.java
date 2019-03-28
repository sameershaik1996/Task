package com.redshift.order.Controller;


import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.redshift.order.Configuration.OrdersConfiguration;
import com.redshift.order.Data.OrdersRepository;
import com.redshift.order.Domain.Orders;
import com.redshift.order.ExceptionHandling.RedshiftExceptionHandling;
import com.redshift.order.Model.OrderDto;
import com.redshift.order.Model.PaymentModel;
import com.redshift.order.Response.RedshiftResponse;
import com.redshift.order.RedshiftConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import javax.xml.ws.Response;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrdersController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    OrdersConfiguration ordersConfiguration;
    @Autowired
    OrdersRepository _orderRespository;

    @Autowired
    RestTemplate restTemplate;


    @RequestMapping(value = "{id}", method = RequestMethod.GET)
    public OrderDto GetOrder(@PathVariable(value = "id") Long id) {



        Orders order = _orderRespository.findById(id).orElseThrow(() -> new RedshiftExceptionHandling("order", "id", id));
        String uri =  ordersConfiguration.getPaymentUrl()+"/api/payments/orders/" + order.getId();

        PaymentModel paymentMethods = restTemplate.getForObject(uri, PaymentModel.class);
        return new OrderDto(order, paymentMethods);
    }

   @HystrixCommand(fallbackMethod = "handleFailure")

    @RequestMapping(value = "{id}", method = RequestMethod.PUT)
    public RedshiftResponse updateOrder(@PathVariable(value = "id") Long id, @Valid @RequestBody OrderDto orderDto) {
        Orders current_order = _orderRespository.findById(id).orElseThrow(() -> new RedshiftExceptionHandling("order", "id", id));
        String uri = ordersConfiguration.getPaymentUrl()+"/api/payments/orders/{id}";
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", "" + current_order.getId());
        restTemplate.put(uri, orderDto.getPaymentModel(), params);
        PaymentModel paymentMethods = restTemplate.getForObject(uri, PaymentModel.class, params);
        if (paymentMethods.getId() != null){
            //System.out.println(paymentMethods);
            Orders order = orderDto.getOrders();
            current_order.setOrder_sub_total(order.getOrder_sub_total());
            current_order.setOrder_tax(order.getOrder_tax());
            current_order.setOrder_total(order.getOrder_total());

            Orders updated_order = _orderRespository.save(current_order);
            logger.info("updated order with id" + updated_order.getId());
            return new RedshiftResponse<>("200", "success", new OrderDto(updated_order, paymentMethods));
        } else {
            return new RedshiftResponse<>("500", "failure", "payment service is down");
        }

    }

       @HystrixCommand(fallbackMethod = "handleFailure")
    @RequestMapping(value = "", method = RequestMethod.POST)
    public RedshiftResponse createOrder(@Valid @RequestBody OrderDto orderDto) {


        Orders order = orderDto.getOrders();

        Orders created_order = _orderRespository.save(order);
        logger.info("created order with id:" + created_order.getId());
        PaymentModel pay = orderDto.getPaymentModel();
        pay.setOrder_id(created_order.getId());
        try {
            PaymentModel paymentModel = (restTemplate.postForObject( ordersConfiguration.getPaymentUrl()+"/api/payments", pay, PaymentModel.class));
            System.out.println(paymentModel.getId());
            if (paymentModel.getId() != null) {

                return new RedshiftResponse<OrderDto>("200", "success", new OrderDto(created_order, paymentModel));
            } else {
                deleteOrder(created_order.getId());
                return new RedshiftResponse<>("200", "success", "order could not be placed");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new RedshiftResponse<>("200", "success", "order could not be placed");


    }


    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteOrder(@PathVariable(value = "id") Long id) {
        logger.info("deleting order with id :" + id);

        Orders order = _orderRespository.findById(id).orElseThrow(() -> new RedshiftExceptionHandling("order", "id", id));
        _orderRespository.delete(order);
        return ResponseEntity.ok().build();
    }

    public RedshiftResponse handleFailure(OrderDto orderDto) {
        return new RedshiftResponse("500", "could not make payment due to time out", orderDto);
    }

    public RedshiftResponse handleFailure(Long id, OrderDto orderDto) {
        return new RedshiftResponse("500", "fall back-- order with id" + id + " could not be updated", orderDto);
    }
}
