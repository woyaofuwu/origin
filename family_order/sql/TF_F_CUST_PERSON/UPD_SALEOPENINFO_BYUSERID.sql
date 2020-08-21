UPDATE tf_f_cust_person
   SET pspt_type_code=:PSPT_TYPE_CODE,pspt_id=:PSPT_ID,cust_name=:CUST_NAME  
 WHERE partition_id=mod(TO_NUMBER(:CUST_ID),10000)
   AND cust_id=TO_NUMBER(:CUST_ID)