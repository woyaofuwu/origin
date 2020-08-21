selecta.service_id,to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date
  from tf_f_user_svc a 
 where a.user_id = :USER_ID
   and a.partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   and ((:ELEMENT_ID is null) or (a.service_id = :ELEMENT_ID))
   and a.product_id = :PRODUCT_ID
   and sysdate between a.start_date and a.end_date
