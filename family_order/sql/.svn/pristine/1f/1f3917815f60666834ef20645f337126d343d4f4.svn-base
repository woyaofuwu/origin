select * from tf_f_user_other o
 where o.partition_id = mod(TO_NUMBER(:USER_ID),10000)
   and o.user_id = :USER_ID
   and o.rsrv_value_code = :RSRV_VALUE_CODE
   and sysdate between o.start_date and o.end_date