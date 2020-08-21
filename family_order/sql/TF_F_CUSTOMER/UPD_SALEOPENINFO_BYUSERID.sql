UPDATE tf_f_customer
   SET cust_name=:CUST_NAME,pspt_type_code=:PSPT_TYPE_CODE,pspt_id=:PSPT_ID,develop_depart_id=:DEVELOP_DEPART_ID,develop_staff_id=:DEVELOP_STAFF_ID,in_depart_id=:IN_DEPART_ID,in_staff_id=:IN_STAFF_ID,in_date=TO_DATE(:IN_DATE, 'YYYY-MM-DD HH24:MI:SS')  
 WHERE partition_id=mod(TO_NUMBER(:CUST_ID),10000)
   AND cust_id=TO_NUMBER(:CUST_ID)