UPDATE tf_f_cust_person
   SET pspt_end_date=TO_DATE(:PSPT_END_DATE, 'YYYY-MM-DD HH24:MI:SS'),pspt_addr=:PSPT_ADDR,sex=:SEX,home_address=:HOME_ADDRESS
 WHERE cust_id=TO_NUMBER(:CUST_ID)
   AND partition_id = MOD(TO_NUMBER(:CUST_ID),10000)