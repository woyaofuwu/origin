SELECT eparchy_code,to_char(derate_id) derate_id,to_char(user_id) user_id,partition_id,acyc_id,derate_mode,to_char(derate_fee) derate_fee,to_char(derate_per) derate_per,to_char(derate_time,'yyyy-mm-dd hh24:mi:ss') derate_time,derate_eparchy_code,derate_city_code,derate_depart_id,derate_staff_id,derate_reason_code,remark 
  FROM tf_a_deratelatefeelog
 WHERE eparchy_code=:EPARCHY_CODE
   AND user_id=TO_NUMBER(:USER_ID)
   AND partition_id=:PARTITION_ID
   AND acyc_id=:ACYC_ID