select PSPT_ID
	   ,PSPT_TYPE_CODE 
  from tf_f_customer 
 WHERE CUST_ID =:CUST_ID 
   AND REMOVE_TAG = '0'