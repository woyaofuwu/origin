SELECT eparchy_code,to_char(derate_id) derate_id,to_char(user_id) user_id,partition_id,acyc_id,derate_mode,to_char(derate_fee) derate_fee,to_char(derate_per) derate_per,to_char(derate_time,'yyyy-mm-dd hh24:mi:ss') derate_time,derate_eparchy_code,derate_city_code,derate_depart_id,derate_staff_id,derate_reason_code,remark 
  FROM tf_a_deratelatefeelog
 WHERE derate_staff_id >= :X_START_STAFF_ID
 AND derate_staff_id <= :X_END_STAFF_ID
 AND derate_time >= to_date(:BEGIN_TIME,'yyyy-mm-dd hh24:mi:ss')
 AND derate_time <= to_date(:END_TIME,'yyyy-mm-dd hh24:mi:ss')