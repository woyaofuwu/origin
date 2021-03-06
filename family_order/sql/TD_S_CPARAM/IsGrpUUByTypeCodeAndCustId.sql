SELECT COUNT(1) RECORDCOUNT FROM TF_F_USER U
WHERE 1=1
AND U.CUST_ID = :CUST_ID
AND U.PRODUCT_ID = :PRODUCT_ID    
AND U.REMOVE_TAG = '0'
AND EXISTS 
  (SELECT UU.USER_ID_A FROM TF_F_RELATION_UU  UU
      WHERE 1=1
      AND UU.PARTITION_ID = MOD(:USER_ID_B,10000)
      AND  UU.USER_ID_B = :USER_ID_B
      AND UU.USER_ID_A = U.USER_ID
      AND UU.RELATION_TYPE_CODE = :RELATION_TYPE_CODE
      AND UU.END_DATE > SYSDATE
  )