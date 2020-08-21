INSERT INTO TS_M_TRADEFEE_IN 
      (trade_id,trade_type_code,in_mode_code,product_id,brand_code,fee_time,
       fee_staff_id,fee_depart_id,fee_state,cancel_tag,cancel_staff_id)
SELECT a.trade_id,a.trade_type_code,a.in_mode_code,a.product_id,a.brand_code,a.fee_time,
       b.staff_id,b.depart_id,a.fee_state,a.cancel_tag,a.cancel_staff_id
  FROM tf_bh_trade_staff a ,ts_m_staff_clcttime_temp b
 WHERE b.depart_id >= :start_depart_id
   AND b.depart_id <= :end_depart_id
   AND b.clct_day = :clct_day
   AND a.trade_staff_id = b.staff_id
   AND a.accept_date >= b.start_date
   AND a.accept_date <= b.end_date 
   AND a.cancel_tag IN ('0','1')
   AND a.fee_state != '0'
 UNION ALL
SELECT a.trade_id,a.trade_type_code,a.in_mode_code,a.product_id,a.brand_code,a.fee_time,
       b.staff_id,b.depart_id,a.fee_state,a.cancel_tag,a.cancel_staff_id
  FROM tf_b_trade a ,ts_m_staff_clcttime_temp b
 WHERE b.depart_id >= :start_depart_id
   AND b.depart_id <= :end_depart_id
   AND b.clct_day = :clct_day
   AND a.trade_staff_id = b.staff_id
   AND a.accept_date >= b.start_date
   AND a.accept_date <= b.end_date 
   AND a.cancel_tag IN ('0','1')
   AND a.fee_state != '0'