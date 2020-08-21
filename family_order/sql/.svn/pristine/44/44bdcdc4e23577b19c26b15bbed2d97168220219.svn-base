SELECT A.*
  FROM TD_S_COMMPARA A
 WHERE 1 = 1
   AND A.SUBSYS_CODE = 'CSM'
   AND A.PARAM_ATTR = '174'
   AND A.PARA_CODE1 = :PARA_CODE1
   AND A.PARAM_CODE IN (SELECT B.PRODUCT_ID
                          FROM TF_F_USER_PRODUCT B
                         WHERE 1 = 1
                           AND B.USER_ID = :USER_ID
                           AND B.PARTITION_ID = MOD(:USER_ID, 10000)
                           AND A.END_DATE > SYSDATE
                           AND A.START_DATE < SYSDATE)
