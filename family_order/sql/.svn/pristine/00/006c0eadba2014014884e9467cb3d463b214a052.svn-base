SELECT COUNT(A.USER_ID) OPEN_NUM
  FROM TF_F_USER A , TF_F_USER_PRODUCT P
 WHERE A.CUST_ID IN (SELECT B.CUST_ID
                       FROM TF_F_CUSTOMER B, TF_F_CUST_PERSON C
                      WHERE B.CUST_ID = C.CUST_ID
                        AND B.PARTITION_ID = C.PARTITION_ID
                        AND B.PSPT_TYPE_CODE = :PSPT_TYPE_CODE
                        AND B.PSPT_ID = :PSPT_ID
                        AND ROWNUM < 30
                        )
   AND A.REMOVE_TAG in ( '0','3' ) 
   AND A.USER_ID=P.USER_ID
   AND A.PARTITION_ID=P.PARTITION_ID
   AND P.BRAND_CODE LIKE 'G%' 
   AND A.EPARCHY_CODE = :EPARCHY_CODE