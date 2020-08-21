INSERT INTO TF_F_USER(PARTITION_ID,USER_ID,CUST_ID,USECUST_ID,BRAND_CODE,PRODUCT_ID,EPARCHY_CODE,CITY_CODE,CITY_CODE_A,USER_DIFF_CODE,USER_PASSWD,USER_TYPE_CODE,USER_TAG_SET,USER_STATE_CODESET,NET_TYPE_CODE,SERIAL_NUMBER,SCORE_VALUE,CONTRACT_ID,CREDIT_CLASS,BASIC_CREDIT_VALUE,CREDIT_VALUE,CREDIT_CONTROL_ID,ACCT_TAG,PREPAY_TAG,MPUTE_MONTH_FEE,MPUTE_DATE,FIRST_CALL_TIME,LAST_STOP_TIME,CHANGEUSER_DATE,IN_NET_MODE,IN_DATE,IN_STAFF_ID,IN_DEPART_ID,OPEN_DATE,REMOVE_TAG,OPEN_MODE,OPEN_STAFF_ID,OPEN_DEPART_ID,DEVELOP_STAFF_ID,DEVELOP_DEPART_ID,DEVELOP_DATE,DEVELOP_CITY_CODE,DEVELOP_EPARCHY_CODE,DEVELOP_NO,ASSURE_CUST_ID,ASSURE_TYPE_CODE,ASSURE_DATE,UPDATE_TIME)
SELECT MOD(TO_NUMBER(USER_ID), 10000) USER_ID,TO_CHAR(USER_ID) USER_ID,TO_CHAR(CUST_ID) CUST_ID,TO_CHAR(USECUST_ID) USECUST_ID,BRAND_CODE,PRODUCT_ID,EPARCHY_CODE,CITY_CODE,CITY_CODE_A,USER_PASSWD,USER_DIFF_CODE,USER_TYPE_CODE,USER_TAG_SET,USER_STATE_CODESET,NET_TYPE_CODE,SERIAL_NUMBER,TO_CHAR(SCORE_VALUE) SCORE_VALUE,CONTRACT_ID,TO_CHAR(CREDIT_CLASS) CREDIT_CLASS,TO_CHAR(BASIC_CREDIT_VALUE) BASIC_CREDIT_VALUE,TO_CHAR(CREDIT_VALUE) CREDIT_VALUE,TO_CHAR(CREDIT_CONTROL_ID) CREDIT_CONTROL_ID,ACCT_TAG,PREPAY_TAG,MPUTE_MONTH_FEE,MPUTE_DATE,FIRST_CALL_TIME,LAST_STOP_TIME,CHANGEUSER_DATE,IN_NET_MODE,IN_DATE,IN_STAFF_ID,IN_DEPART_ID,OPEN_DATE,REMOVE_TAG,OPEN_MODE,OPEN_STAFF_ID,OPEN_DEPART_ID,DEVELOP_STAFF_ID,DEVELOP_DEPART_ID,DEVELOP_DATE,DEVELOP_CITY_CODE,DEVELOP_EPARCHY_CODE,DEVELOP_NO,TO_CHAR(ASSURE_CUST_ID) ASSURE_CUST_ID,ASSURE_TYPE_CODE,ASSURE_DATE,UPDATE_TIME
    FROM TF_B_TRADE_USER T
   WHERE T.TRADE_ID = :TRADE_ID
     AND T.ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))