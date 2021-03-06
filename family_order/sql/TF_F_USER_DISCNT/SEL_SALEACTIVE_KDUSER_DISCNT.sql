SELECT B.USER_ID,
       B.DISCNT_CODE,
       TO_CHAR(B.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,
       TO_CHAR(B.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,
       B.INST_ID,
       B.USER_ID_A
  FROM TF_F_USER A, TF_F_USER_DISCNT B
 WHERE EXISTS (SELECT 1
          FROM TD_S_COMMPARA A2
         WHERE A2.SUBSYS_CODE = 'CSM'
           AND A2.PARAM_ATTR = 943
           AND A2.PARAM_CODE = '1'
           AND A2.PARA_CODE1 = :PACKAGE_ID
           AND A2.PARA_CODE2 = :ELEMENT_ID
           AND B.DISCNT_CODE = TO_CHAR(A2.PARA_CODE3)
           AND B.END_DATE > SYSDATE
           AND SYSDATE BETWEEN A2.START_DATE AND A2.END_DATE)
   AND A.USER_ID = B.USER_ID
   AND A.PARTITION_ID = B.PARTITION_ID
   AND A.REMOVE_TAG = '0'
   AND B.END_DATE > SYSDATE
   AND A.SERIAL_NUMBER IN
       (SELECT 'KD_' || A1.SERIAL_NUMBER
          FROM TF_F_USER A1
         WHERE A1.PARTITION_ID = MOD(:USER_ID, 10000)
           AND A1.USER_ID = :USER_ID
           AND A1.REMOVE_TAG = '0')