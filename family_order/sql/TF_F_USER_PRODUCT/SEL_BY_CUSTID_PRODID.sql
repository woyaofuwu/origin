SELECT U.SERIAL_NUMBER, P.PRODUCT_ID,U.EPARCHY_CODE
  FROM TF_F_USER U, TF_F_USER_PRODUCT P
 WHERE 1 = 1
   AND U.USER_ID = P.USER_ID
   AND U.PARTITION_ID = MOD(TO_NUMBER(U.USER_ID), 10000)
   AND U.REMOVE_TAG = '0'
   AND U.CUST_ID = :CUST_ID
   AND P.PRODUCT_ID = :PRODUCT_ID