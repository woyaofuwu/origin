SELECT A.USER_ID,
       A.PRODUCT_ID,
       A.PACKAGE_ID,
       A.RELATION_TRADE_ID,
       TO_CHAR(A.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,
       PARAM_CODE,
       PARA_CODE1,
       PARA_CODE2,
       PARA_CODE3
  FROM TF_F_USER_SALE_ACTIVE A, TD_S_COMMPARA B
 WHERE A.USER_ID = :USER_ID
   AND A.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000)
   AND A.PROCESS_TAG = '0'
   AND A.END_DATE > SYSDATE
   AND A.PRODUCT_ID = B.PARAM_CODE
   AND A.PACKAGE_ID = B.PARA_CODE1
   AND B.END_DATE > SYSDATE
   AND B.PARAM_ATTR = '50'
   AND A.PACKAGE_ID not in
       (SELECT d.PACKAGE_ID
          FROM TF_F_USER_SALE_ACTIVE d, TD_S_COMMPARA e
         WHERE d.USER_ID = :USER_ID
           AND d.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000)
           AND d.PROCESS_TAG = '0'
           AND d.END_DATE > SYSDATE
           AND d.PRODUCT_ID = e.PARAM_CODE
           AND d.PACKAGE_ID = e.PARA_CODE1
           AND e.END_DATE > SYSDATE
           AND e.PARAM_ATTR = '50'
           AND exists
         (SELECT 1
                  FROM TF_F_USER_SALE_GOODS f
                 WHERE f.USER_ID = d.USER_ID
                   AND f.RELATION_TRADE_ID = d.RELATION_TRADE_ID
                   AND f.RSRV_DATE2 > SYSDATE
                   AND f.PRODUCT_ID = e.PARAM_CODE
                   AND f.PACKAGE_ID = e.PARA_CODE1
                    AND (f.RSRV_STR1 = 'Y' or f.RES_CODE = e.PARA_CODE2)))