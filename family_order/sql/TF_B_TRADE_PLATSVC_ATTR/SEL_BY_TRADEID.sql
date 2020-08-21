select to_char(e.TRADE_ID) TRADE_ID,
       e.ACCEPT_MONTH,
       to_char(e.USER_ID) USER_ID,
       e.SERVICE_ID,
       e.SERIAL_NUMBER,
       e.INFO_CODE,
       decode(e.INFO_CODE, '001',decode(e.RSRV_STR2,'',e.INFO_VALUE,e.RSRV_STR2),e.INFO_VALUE )  INFO_VALUE ,
       e.INFO_NAME,
       to_char(e.UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,
       e.UPDATE_STAFF_ID,
       e.UPDATE_DEPART_ID,
       e.REMARK,
       e.RSRV_NUM1,
       e.RSRV_NUM2,
       e.RSRV_STR1,
       e.RSRV_STR2,
       to_char(e.RSRV_DATE1, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1,
       to_char(e.RSRV_DATE2, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2,
       to_char(e.RSRV_DATE3, 'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3
  from tf_b_trade_platsvc_attr e
  WHERE 
   E.trade_id=TO_NUMBER(:TRADE_ID)
   and e.info_value is not null
   and length(e.info_value) > 0
   AND E.accept_month=to_number(substr(:TRADE_ID,5,2))
   AND E.SERVICE_ID IN 
  (SELECT S.SERVICE_ID FROM TF_B_TRADE_PLATSVC s WHERE 
   E.service_id = S.service_id
   and S.trade_id=TO_NUMBER(:TRADE_ID)
   AND S.accept_month=to_number(substr(:TRADE_ID,5,2))
   )