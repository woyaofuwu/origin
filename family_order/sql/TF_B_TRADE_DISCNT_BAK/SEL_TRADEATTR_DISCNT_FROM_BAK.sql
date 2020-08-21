SELECT 
a.RELA_INST_ID,
a.TRADE_ID,
a.ACCEPT_MONTH,
a.PARTITION_ID,
a.USER_ID,
a.ATTR_VALUE,
a.ATTR_CODE,
a.INST_ID,
a.INST_TYPE,
to_char(a.START_DATE,'yyyy-MM-dd HH24:mi:ss') START_DATE,
to_char(a.END_DATE,'yyyy-MM-dd HH24:mi:ss') END_DATE,
to_char(a.UPDATE_TIME,'yyyy-MM-dd HH24:mi:ss') UPDATE_TIME,
a.UPDATE_STAFF_ID,
a.UPDATE_DEPART_ID,
a.REMARK,
a.RSRV_NUM1,
a.RSRV_NUM2,
a.RSRV_NUM3,
a.RSRV_NUM4,
a.RSRV_NUM5,
a.RSRV_STR1,
a.RSRV_STR2,
a.RSRV_STR3,
a.RSRV_STR4,
a.RSRV_STR5,
to_char(a.RSRV_DATE1,'yyyy-MM-dd HH24:mi:ss') RSRV_DATE1,
to_char(a.RSRV_DATE2,'yyyy-MM-dd HH24:mi:ss') RSRV_DATE2,
to_char(a.RSRV_DATE3,'yyyy-MM-dd HH24:mi:ss') RSRV_DATE3,
a.RSRV_TAG1,
a.RSRV_TAG2,
a.RSRV_TAG3
From tf_b_trade_attr_bak a,tf_b_trade_discnt_bak b 
Where a.trade_id = TO_NUMBER(:TRADE_ID)
  AND b.trade_id = TO_NUMBER(:TRADE_ID)
  AND a.RELA_INST_ID =  b.inst_id
  And a.accept_month = b.accept_month
  And a.user_id=:USER_ID
  AND a.end_date > Sysdate
  AND b.end_date > Sysdate
  