UPDATE tf_f_customer
   SET remove_tag='0',remove_date=''
 WHERE cust_id=TO_NUMBER(:CUST_ID)  AND PARTITION_ID=MOD(TO_NUMBER(:CUST_ID),10000)
   AND remove_tag='1'