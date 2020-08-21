SELECT COUNT(1) RECORDCOUNT
  FROM TD_B_PRODUCT_SVC
 WHERE DEFAULT_TAG = '1'
   AND SERVICE_ID = :SERVICE_ID
   AND END_DATE > SYSDATE
   AND PRODUCT_ID = (SELECT /*+index(t,PK_TF_F_USER)*/
                      PRODUCT_ID
                       FROM TF_F_USER T
                      WHERE USER_ID = :USER_ID
                        AND PARTITION_ID = MOD(:USER_ID, 10000)
                        AND REMOVE_TAG = '0')