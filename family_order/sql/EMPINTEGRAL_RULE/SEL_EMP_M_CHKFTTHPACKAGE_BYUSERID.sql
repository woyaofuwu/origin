--IS_CACHE=N
SELECT COUNT(1) 统计数
  FROM (SELECT 1
          FROM TF_F_USER_SALE_ACTIVE A
         WHERE A.SERIAL_NUMBER = :MOBILE_NUM
           AND A.PRODUCT_ID = '67220428'
           AND A.PROCESS_TAG = '0'
           AND A.END_DATE > SYSDATE
        UNION
        SELECT 1
          FROM TF_F_USER_DISCNT D
         WHERE D.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000)
           AND D.USER_ID = TO_NUMBER(:USER_ID)
           AND D.END_DATE > SYSDATE
           AND EXISTS (SELECT 1
                  FROM TD_S_COMMPARA C
                 WHERE C.SUBSYS_CODE = 'CSM'
                   AND C.PARAM_ATTR = 150
                   AND C.PARAM_CODE = 'CUMU'
                   AND C.PARA_CODE1 = D.DISCNT_CODE
                   AND C.END_DATE > SYSDATE))