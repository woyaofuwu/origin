SELECT 
*
FROM TF_F_CUST_PERSON_OTHER a
 WHERE cust_id=to_number(:CUST_ID)
   AND partition_id = MOD(to_number(:CUST_ID),10000)