SELECT TRADE_ID,
       ACCEPT_MONTH,
       USER_ID,
       ACCT_DAY,
       CHG_TYPE,
       CHG_MODE,
       to_char(CHG_DATE, 'yyyy-mm-dd hh24:mi:ss') CHG_DATE,
       to_char(FIRST_DATE, 'yyyy-mm-dd hh24:mi:ss') FIRST_DATE,
       INST_ID,
       to_char(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,
       to_char(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,
       MODIFY_TAG,
       to_char(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
       UPDATE_STAFF_ID,
       UPDATE_DEPART_ID,
       REMARK,
       RSRV_NUM1,
       RSRV_NUM2,
       RSRV_NUM3,
       RSRV_NUM4,
       RSRV_NUM5,
       RSRV_STR1,
       RSRV_STR2,
       RSRV_STR3,
       RSRV_STR4,
       RSRV_STR5,
       to_char(RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1,
       to_char(RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2,
       to_char(RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3,
       RSRV_TAG1,
       RSRV_TAG2,
       RSRV_TAG3
  FROM TF_B_TRADE_USER_ACCTDAY
 WHERE trade_id = :TRADE_ID
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))
   AND modify_tag != '9'