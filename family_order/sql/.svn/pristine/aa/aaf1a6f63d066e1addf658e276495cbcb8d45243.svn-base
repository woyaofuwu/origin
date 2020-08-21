Select to_char(TRADE_ID) TRADE_ID, ACCEPT_MONTH, to_char(USER_ID) USER_ID, INST_TYPE, to_char(INST_ID) INST_ID, ATTR_CODE, ATTR_VALUE, to_char(START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE, to_char(END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE, MODIFY_TAG, to_char(UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, UPDATE_STAFF_ID, UPDATE_DEPART_ID, REMARK, RSRV_NUM1, RSRV_NUM2, RSRV_NUM3, to_char(RSRV_NUM4) RSRV_NUM4, to_char(RSRV_NUM5) RSRV_NUM5,b.vresult RSRV_STR1, RSRV_STR2, RSRV_STR3, RSRV_STR4, RSRV_STR5, to_char(RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, to_char(RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, to_char(RSRV_DATE3,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, RSRV_TAG1, RSRV_TAG2, RSRV_TAG3  
FROM   TF_B_TRADE_ATTR a,td_s_cparam b
WHERE TRADE_ID= TO_NUMBER(:TRADE_ID)
AND   ACCEPT_MONTH = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
AND   INST_TYPE = 'S' 
  AND a.attr_value = b.value1
   AND b.KEY=:KEY
   AND (b.value2=:EPARCHY_CODE OR b.value2='ZZZZ')