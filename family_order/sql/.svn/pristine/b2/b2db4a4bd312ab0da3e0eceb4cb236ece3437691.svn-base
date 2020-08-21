INSERT INTO TS_S_PFEE_DAY_STAFF
      (clct_day,staff_id,date_id,depart_id,city_code_ed,charge_source_code,pay_fee_mode_code,
       deposit_code,sale_num,sale_money,agent_fee,fee,cancel_tag,remove_tag)
SELECT :clct_day,b.staff_id,TO_NUMBER(SUBSTR(:clct_day,5,2)),b.depart_id,a.city_code,a.charge_source_code,a.pay_fee_mode_code,
       a.deposit_code,-COUNT(a.charge_id) sale_num,SUM(a.recv_fee) sale_money,null agent_fee,SUM(a.recv_fee) fee,'2' cancel_tag,NULL
  FROM tf_a_paylog a, ts_m_staff_clcttime_temp b
 WHERE b.clct_day = :clct_day
   AND b.depart_id >= :start_depart_id
   AND b.depart_id <= :end_depart_id
   AND a.recv_staff_id = b.staff_id 
   AND a.recv_time >= b.start_date
   AND a.recv_time <  b.end_date   
   AND a.partition_id = TO_NUMBER(SUBSTR(:clct_day,5,2))
   AND a.cancel_tag IN ('2')  
 GROUP BY a.city_code,b.staff_id,b.depart_id,a.charge_source_code,a.pay_fee_mode_code,a.deposit_code