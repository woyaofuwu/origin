SELECT PARTITION_ID,
       TO_CHAR(ACCT_ID) ACCT_ID,
       TO_CHAR(CUST_ID) CUST_ID,
       PAY_NAME,
       PAY_MODE_CODE,
       ACCT_PASSWD,
       NET_TYPE_CODE,
       EPARCHY_CODE,
       CITY_CODE,
       BANK_CODE,
       BANK_ACCT_NO,
       TO_CHAR(SCORE_VALUE) SCORE_VALUE,
       CREDIT_CLASS_ID,
       TO_CHAR(BASIC_CREDIT_VALUE) BASIC_CREDIT_VALUE,
       TO_CHAR(CREDIT_VALUE) CREDIT_VALUE,
       TO_CHAR(DEBUTY_USER_ID) DEBUTY_USER_ID,
       DEBUTY_CODE,
       CONTRACT_NO,
       DEPOSIT_PRIOR_RULE_ID,
       ITEM_PRIOR_RULE_ID,
       TO_CHAR(OPEN_DATE, 'yyyy-mm-dd hh24:mi:ss') OPEN_DATE,
       REMOVE_TAG,
       TO_CHAR(REMOVE_DATE, 'yyyy-mm-dd hh24:mi:ss') REMOVE_DATE,
       UPDATE_STAFF_ID,
       UPDATE_DEPART_ID,
       TO_CHAR(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
       REMARK,
       RSRV_STR1,
       RSRV_STR2,
       RSRV_STR3,
       RSRV_STR4,
       RSRV_STR5,
       RSRV_STR6,
       RSRV_STR7,
       RSRV_STR8,
       RSRV_STR9,
       RSRV_STR10
  FROM TF_F_ACCOUNT
 WHERE ACCT_ID = TO_NUMBER((SELECT ACCT_ID
                             FROM TF_A_PAYRELATION A, TF_F_USER B
                            WHERE A.USER_ID = B.USER_ID
                              AND A.DEFAULT_TAG = '1'
                              AND A.ACT_TAG = '1'
                              AND TO_CHAR(SYSDATE, 'YYYYMMDD') BETWEEN
                                  A.START_CYCLE_ID AND A.END_CYCLE_ID
                              AND B.SERIAL_NUMBER = :SERIAL_NUMBER
                              AND B.REMOVE_TAG = '0'))