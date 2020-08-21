UPDATE tf_f_customer
   SET rsrv_str10=:RSRV_STR10  
 WHERE partition_id=mod(TO_NUMBER(:CUST_ID),10000)
   AND cust_id=TO_NUMBER(:CUST_ID)