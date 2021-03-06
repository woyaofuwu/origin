SELECT to_char(TRADE_ID) TRADE_ID,to_char(SUBSCRIBE_ID) SUBSCRIBE_ID,to_char(BPM_ID) BPM_ID,TRADE_TYPE_CODE,IN_MODE_CODE,PRIORITY,SUBSCRIBE_STATE,NEXT_DEAL_TAG,PRODUCT_ID,BRAND_CODE,to_char(USER_ID) USER_ID,to_char(CUST_ID) CUST_ID,to_char(ACCT_ID) ACCT_ID,SERIAL_NUMBER,CUST_NAME,to_char(ACCEPT_DATE,'yyyy-mm-dd hh24:mi:ss') ACCEPT_DATE,ACCEPT_MONTH,TRADE_STAFF_ID,TRADE_DEPART_ID,TRADE_CITY_CODE,TRADE_EPARCHY_CODE,TERM_IP,EPARCHY_CODE,CITY_CODE,OLCOM_TAG,to_char(EXEC_TIME,'yyyy-mm-dd hh24:mi:ss') EXEC_TIME,to_char(FINISH_DATE,'yyyy-mm-dd hh24:mi:ss') FINISH_DATE,to_char(OPER_FEE) OPER_FEE,to_char(FOREGIFT) FOREGIFT,to_char(ADVANCE_PAY) ADVANCE_PAY,INVOICE_NO,FEE_STATE,to_char(FEE_TIME,'yyyy-mm-dd hh24:mi:ss') FEE_TIME,FEE_STAFF_ID,CANCEL_TAG,to_char(CANCEL_DATE,'yyyy-mm-dd hh24:mi:ss') CANCEL_DATE,CANCEL_STAFF_ID,CANCEL_DEPART_ID,CANCEL_CITY_CODE,CANCEL_EPARCHY_CODE,PROCESS_TAG_SET,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_STR6,RSRV_STR7,RSRV_STR8,RSRV_STR9,RSRV_STR10,REMARK
  FROM tf_bh_trade 
  where (trade_id,accept_month) =
(select max(a.trade_id),to_number(substr(to_char(max(a.trade_id)),5,2)) from tf_bh_trade a where
 a.user_id = :USER_ID
 and a.cancel_tag='0'
 and a.ACCEPT_DATE = to_date(:SYS_DATE,'yyyy-mm-dd hh24:mi:ss')
)