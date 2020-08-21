Select to_char(TRADE_ID) TRADE_ID, ACCEPT_MONTH, to_char(USER_ID) USER_ID, to_char(USER_ID_A) USER_ID_A, DISCNT_CODE, SPEC_TAG, RELATION_TYPE_CODE, to_char(INST_ID) INST_ID, to_char(CAMPN_ID) CAMPN_ID, to_char(START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE, to_char(END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE, MODIFY_TAG, to_char(UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_NUM1, RSRV_NUM2, RSRV_NUM3, to_char(RSRV_NUM4) RSRV_NUM4, to_char(RSRV_NUM5) RSRV_NUM5, RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, to_char(RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, to_char(RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, to_char(RSRV_DATE3,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, RSRV_TAG1, RSRV_TAG2, RSRV_TAG3, MODIFY_TAG mod_type
  FROM tf_b_trade_discnt a
 WHERE a.trade_id=TO_NUMBER(:TRADE_ID)
   AND a.accept_month=TO_NUMBER(:ACCEPT_MONTH)
   AND a.modify_tag=:MODIFY_TAG
   AND exists (SELECT 1 FROM td_s_commpara b 
               WHERE b.subsys_code = 'CSM'
               AND b.param_attr = 1078
               AND b.param_code = to_char(a.discnt_code)
               AND sysdate BETWEEN b.start_date AND b.end_date)
