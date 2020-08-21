INSERT INTO TS_M_TRADEFEE_IN 
      (trade_id,trade_type_code,in_mode_code,product_id,
       brand_code,fee_time,fee_staff_id,fee_depart_id,fee_state,cancel_tag,cancel_staff_id)
SELECT a.trade_id,a.trade_type_code,a.in_mode_code,a.product_id,a.brand_code,
       a.fee_time,a.fee_staff_id,b.depart_id,fee_state,'0' cancel_tag,null cancel_staff_id
  FROM tf_bh_trade_staff a ,ts_m_staff_clcttime_temp b
 WHERE b.clct_day = :clct_day
   AND b.depart_id >= :start_depart_id
   AND b.depart_id <= :end_depart_id
   AND a.trade_staff_id = b.staff_id
   AND a.accept_date >= b.start_date
   AND a.accept_date <= b.end_date 
   AND a.trade_type_code = 330 
   AND a.cancel_tag = '0' --积分兑奖不存在返销