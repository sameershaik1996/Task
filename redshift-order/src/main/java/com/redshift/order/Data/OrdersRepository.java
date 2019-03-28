package com.redshift.order.Data;

import com.redshift.order.Domain.Orders;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrdersRepository  extends  JpaRepository<Orders,Long>{


}
