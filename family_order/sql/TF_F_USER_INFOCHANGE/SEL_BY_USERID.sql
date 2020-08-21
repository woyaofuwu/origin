SELECT inst_id,partition_id,to_char(user_id) user_id,product_id,brand_code,serial_number,imsi,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,trade_type_code,relation_trade_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,UPDATE_STAFF_ID,UPDATE_DEPART_ID
  FROM tf_f_user_infochange
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND end_date >= SYSDATE
 ORDER BY start_date