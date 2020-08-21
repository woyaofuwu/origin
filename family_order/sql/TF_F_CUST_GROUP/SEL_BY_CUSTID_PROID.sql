SELECT cust.GROUP_ID,
       cust.CUST_ID,
       cust.CUST_NAME,
       cust.CLASS_ID,
       cust.JURISTIC_TYPE_CODE,
       cust.JURISTIC_NAME,
       cust.ENTERPRISE_TYPE_CODE,
       cust.CALLING_TYPE_CODE,
       u.PRODUCT_ID,
       u.SERIAL_NUMBER,
       u.USER_ID
  FROM TF_F_CUST_GROUP cust,TF_F_USER u
 WHERE 1=1
   AND u.CUST_ID = cust.CUST_ID
   AND cust.CUST_ID = :CUST_ID  
   AND u.REMOVE_TAG='0'
   AND u.PRODUCT_ID = to_number(:PRODUCT_ID)
   AND cust.REMOVE_TAG ='0'