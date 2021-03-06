SELECT VIP_ID,
       TO_CHAR(CUST_ID) CUST_ID,
       TO_CHAR(USECUST_ID) USECUST_ID,
       TO_CHAR(USER_ID) USER_ID,
       SERIAL_NUMBER,
       VIP_CLASS_ID,
       VIP_TYPE_CODE
  FROM TF_F_CUST_VIP
 WHERE CUST_ID = TO_NUMBER(:CUST_ID)
   AND (:REMOVE_TAG IS NULL OR REMOVE_TAG = :REMOVE_TAG)