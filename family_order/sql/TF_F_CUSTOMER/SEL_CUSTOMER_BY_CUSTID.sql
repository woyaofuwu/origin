SELECT T.*
  FROM TF_F_CUSTOMER T
 WHERE T.CUST_ID = :CUST_ID 
   AND T.PARTITION_ID = MOD(TO_NUMBER(:CUST_ID), 10000)