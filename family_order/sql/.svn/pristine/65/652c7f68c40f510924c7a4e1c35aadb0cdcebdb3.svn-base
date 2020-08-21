SELECT to_char(id) id,id_type,partition_id,to_char(user_id) user_id,acyc_id,detail_item_code,to_char(fee) fee,discnt_code,bill_type,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM ts_a_bdetailinfo_9_0                                                                                                                                                                  
 WHERE user_id=TO_NUMBER(:USER_ID)                                                                                                                                                          
   AND acyc_id=:ACYC_ID                                                                                                                                                                     
   AND bill_type IN (:BILL_TYPE)