package com.redshift.payment.Data;

import com.redshift.payment.Domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository extends JpaRepository<Payment,Long> {
    @Query(value="select * from payment where order_id=?1",nativeQuery=true)
    public Payment getPaymentByOrderId(Long id);
}
