INSERT INTO TF_B_TRANSFEE_TEMP
      (LOG_ID,
       SEQ_ID,
       SYS_ID,
       PAY_TYPE,
       USER_ID,
       ACCT_ID,
       SERIAL_NUMBER,
       PAYAMOUNT,
       UNIT,
       REP_TIME,
       RSPCODE)
    VALUES
      (:LOG_ID,
       :SEQ_ID,
       :SYS_ID,
       :PAY_TYPE,
       :USER_ID,
       :ACCT_ID,
       :SERIAL_NUMBER,
       :PAYAMOUNT,
       :UNIT,
       TO_DATE(:REP_TIME, 'yyyy-mm-dd hh24:mi:ss'),
       :RSPCODE)