SELECT to_char(TRADE_ID) TRADE_ID,
       ACCEPT_MONTH,
       to_char(USER_ID) USER_ID,
       to_char(USER_ID_A) USER_ID_A,
       DISCNT_CODE,
       SPEC_TAG,
       RELATION_TYPE_CODE,
       INST_ID,
       CAMPN_ID,
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
       RSRV_DATE1,
       RSRV_DATE2,
       RSRV_DATE3,
       RSRV_TAG1,
       RSRV_TAG2,
       RSRV_TAG3
  FROM tf_b_trade_discnt
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   and accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))