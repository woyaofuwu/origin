SELECT TO_CHAR(CAMPN_ID) CAMPN_ID,CAMPN_CODE,PRODUCT_NAME,PACKAGE_NAME,CAMPN_TYPE,TO_CHAR
       (ACCEPT_DATE, 'yyyy-mm-dd hh24:mi:ss') ACCEPT_DATE,TO_CHAR(START_DATE, 'yyyy-mm-dd hh24:mi:ss') 
       START_DATE,TO_CHAR(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE FROM TF_F_USER_SALE_ACTIVE 
            WHERE user_id = TO_NUMBER(:USER_ID)
            AND partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
            And PROCESS_TAG = '0' AND sysdate <= end_date
            AND NOT EXISTS(SELECT 1 FROM  TD_S_COMMPARA C WHERE C.SUBSYS_CODE = 'CSM'
            AND C.PARAM_ATTR = '12'
            AND C.PARA_CODE1 = 'SJYYT'
            AND C.END_DATE > SYSDATE
            AND C.PARAM_CODE = PACKAGE_ID)
