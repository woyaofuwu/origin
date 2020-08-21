INSERT INTO TS_S_TFEE_DAY_STAFF
      (clct_day,date_id,staff_id,depart_id,brand_code,trade_type_code,fee_mode,
       fee_type_code,device_type_code,res_type_code,sale_num,sale_money,agent_fee,fee,cancel_tag)
SELECT :clct_day,TO_NUMBER(SUBSTR(:clct_day,5,2)),b.fee_staff_id,b.fee_depart_id,b.brand_code,b.trade_type_code,a.fee_mode,
       a.fee_type_code,NULL,NULL,COUNT(b.trade_id),SUM(a.fee) ,NULL,SUM(a.fee) fee ,'0' 
  FROM tf_b_tradefee_sub a ,ts_m_tradefee_in b
 WHERE a.trade_id = b.trade_id 
   AND b.cancel_tag IN ('0','1')
   AND b.fee_depart_id >= :start_depart_id
   AND b.fee_depart_id <= :end_depart_id
 GROUP BY b.fee_staff_id,b.fee_depart_id,b.brand_code,b.trade_type_code,a.fee_mode,a.fee_type_code