SELECT CUST_NAME
  FROM   TF_F_CUST_VIP
 WHERE   CUST_ID=to_number(:CUST_ID)
   And (:REMOVE_TAG IS NULL OR REMOVE_TAG = :REMOVE_TAG)