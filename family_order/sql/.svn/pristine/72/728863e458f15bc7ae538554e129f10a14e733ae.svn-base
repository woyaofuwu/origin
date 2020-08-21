UPDATE tf_f_customer
   SET cust_name=:CUST_NAME,pspt_type_code=:PSPT_TYPE_CODE,pspt_id=:PSPT_ID,remark=:REMARK  
 WHERE cust_id=TO_NUMBER(:CUST_ID) AND PARTITION_ID=MOD(TO_NUMBER(:CUST_ID),10000)