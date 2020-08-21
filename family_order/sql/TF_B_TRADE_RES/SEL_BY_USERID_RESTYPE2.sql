select b.TRADE_ID,b.ACCEPT_MONTH,
to_char(b.USER_ID) USER_ID, to_char(b.USER_ID_A) USER_ID_A, 
b.RES_TYPE_CODE, b.RES_CODE, b.IMSI, b.KI,b.INST_ID,b.CAMPN_ID,b.MODIFY_TAG,
to_char(b.START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE, 
to_char(b.END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE,
to_char(b.UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, 
b.UPDATE_STAFF_ID, b.UPDATE_DEPART_ID, b.REMARK, 
b.RSRV_NUM1, b.RSRV_NUM2, b.RSRV_NUM3, b.RSRV_NUM4, b.RSRV_NUM5, 
b.RSRV_STR1, b.RSRV_STR2, b.RSRV_STR3, b.RSRV_STR4, b.RSRV_STR5, 
to_char(b.RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, 
to_char(b.RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, 
to_char(b.RSRV_DATE3,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, 
b.RSRV_TAG1, b.RSRV_TAG2, b.RSRV_TAG3
 from tf_b_trade a, tf_b_trade_res b
 where a.trade_id=b.trade_id
 and a.accept_month=b.accept_month
 and a.cancel_tag='0'
 AND b.USER_ID=:USER_ID
 AND b.USER_ID_A=:USER_ID_A
 AND b.RES_TYPE_CODE=:RES_TYPE_CODE
 AND b.RES_CODE = :RES_CODE
 AND b.RSRV_STR4 = :RES_KIND_CODE
 AND sysdate between b.start_date and b.end_date