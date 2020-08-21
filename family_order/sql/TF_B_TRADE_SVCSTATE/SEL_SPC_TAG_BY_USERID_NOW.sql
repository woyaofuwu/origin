SELECT TS.USER_ID,TS.SERVICE_ID,TS.MAIN_TAG,TS.STATE_CODE,TS.START_DATE,TS.END_DATE,TS.UPDATE_TIME,TS.UPDATE_STAFF_ID,TS.UPDATE_DEPART_ID,TS.REMARK,TS.
  RSRV_NUM1,TS.RSRV_NUM2,TS.RSRV_NUM3,TS.RSRV_NUM4,TS.RSRV_NUM5,TS.RSRV_STR1,TS.RSRV_STR2,TS.RSRV_STR3,TS.RSRV_STR4,TS.RSRV_STR5,TS.
  RSRV_DATE1,TS.RSRV_DATE2,TS.RSRV_DATE3,TS.RSRV_TAG1,TS.RSRV_TAG2,TS.RSRV_TAG3,TS.INST_ID
FROM tf_B_trade t,Tf_b_Trade_Svcstate ts
WHERE t.user_id=to_number(:USER_ID)
AND T.TRADE_ID=TS.TRADE_ID
AND TS.ACCEPT_MONTH=T.ACCEPT_MONTH
AND TS.RSRV_STR1='BTorGS'
AND TS.RSRV_TAG1='1'
AND TS.STATE_CODE NOT IN ('J')
AND TS.MAIN_TAG='1') WHERE ROWNUM <2