Select to_char(a.TRADE_ID) TRADE_ID, a.ACCEPT_MONTH, to_char(a.USER_ID) USER_ID, a.INST_TYPE, to_char(a.INST_ID) INST_ID, a.ATTR_CODE, a.ATTR_VALUE, to_char(a.START_DATE,'yyyy-mm-dd hh24:mi:ss') START_DATE, to_char(a.END_DATE,'yyyy-mm-dd hh24:mi:ss') END_DATE, a.MODIFY_TAG, to_char(a.UPDATE_TIME,'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME, a.UPDATE_STAFF_ID, a.UPDATE_DEPART_ID, a.REMARK, a.RSRV_NUM1, a.RSRV_NUM2, a.RSRV_NUM3, to_char(a.RSRV_NUM4) RSRV_NUM4, to_char(a.RSRV_NUM5) RSRV_NUM5, a.RSRV_STR1, a.RSRV_STR2, a.RSRV_STR3, a.RSRV_STR4, a.RSRV_STR5, to_char(a.RSRV_DATE1,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE1, to_char(a.RSRV_DATE2,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE2, to_char(a.RSRV_DATE3,'yyyy-mm-dd hh24:mi:ss') RSRV_DATE3, a.RSRV_TAG1, a.RSRV_TAG2, a.RSRV_TAG3, b.service_id
  FROM tf_b_trade_attr a,tf_b_trade_svc b 
  WHERE a.trade_id = TO_NUMBER(:TRADE_ID)
   AND a.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND a.trade_id = b.trade_id
   AND a.USER_ID = b.USER_ID
   AND b.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   And a.INST_TYPE ='S'
   AND a.modify_tag in ('0','1','2')
   AND a.INST_ID = b.INST_ID
   AND EXISTS (SELECT 1 FROM TD_B_ATTR_ITEMA 
               WHERE ID=b.service_id 
                 AND ID_TYPE='S' 
                 AND ATTR_TYPE_CODE='1' 
                 AND sysdate between start_date and end_date
                 AND (eparchy_code='ZZZZ' or eparchy_code=:EPARCHY_CODE))
