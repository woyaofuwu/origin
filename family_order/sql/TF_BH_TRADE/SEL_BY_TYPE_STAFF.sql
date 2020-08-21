SELECT  '' TRADE_ID,'' ORDER_ID,'' BPM_ID,-1 TRADE_TYPE_CODE,'' IN_MODE_CODE,-1 PRIORITY,
 '' SUBSCRIBE_STATE,'' NEXT_DEAL_TAG,-1 PRODUCT_ID,'' BRAND_CODE,'' USER_ID,'' CUST_ID,
 '' ACCT_ID,'' SERIAL_NUMBER,'' CUST_NAME,'' ACCEPT_DATE,-1 ACCEPT_MONTH,
 '' TRADE_CITY_CODE, '' TRADE_EPARCHY_CODE,'' TERM_IP,'' EPARCHY_CODE,
 '' CITY_CODE,'' OLCOM_TAG,'' EXEC_TIME,'' OPER_FEE,'' FOREGIFT,'' ADVANCE_PAY,
 '' INVOICE_NO,'' FEE_STATE,'' FEE_TIME,'' FEE_STAFF_ID,'' CANCEL_TAG,'' CANCEL_DATE,
 '' CANCEL_STAFF_ID,'' CANCEL_DEPART_ID,'' CANCEL_CITY_CODE,'' CANCEL_EPARCHY_CODE,'' PROCESS_TAG_SET,
 '' RSRV_STR1,'' RSRV_STR2,'' RSRV_STR3,'' RSRV_STR4,'' RSRV_STR5,'' RSRV_STR6,'' RSRV_STR9,'' RSRV_STR10,'' REMARK,
nvl(trade_depart_id,'所有部门') trade_depart_id,
nvl(trade_staff_id,'所有员工') trade_staff_id,
nvl(to_char(trunc(finish_date),'yyyy-mm-dd'),'所有日期') finish_date,
sum(DECODE(rsrv_str1,'0',to_number(rsrv_str2),0)) rsrv_str7, --0-审核通过
sum(DECODE(rsrv_str1,'1',to_number(rsrv_str2),0)) rsrv_str8  --1-拒绝通过
  FROM TF_BH_TRADE
 WHERE TRADE_TYPE_CODE = :TRADE_TYPE_CODE
   AND (TRADE_STAFF_ID = :TRADE_STAFF_ID OR :TRADE_STAFF_ID IS NULL)
   AND (TRADE_DEPART_ID = :TRADE_DEPART_ID OR :TRADE_DEPART_ID IS NULL)
   AND FINISH_DATE >= to_date(:START_TIME,'yyyy-mm-dd hh24:mi:ss')
   AND FINISH_DATE <= to_date(:END_TIME,'yyyy-mm-dd hh24:mi:ss')
   group by trade_depart_id,trade_staff_id,trunc(finish_date)
   /*group by grouping sets 
   ((trade_depart_id,trade_staff_id,trunc(finish_date)),
    (trade_depart_id,trade_staff_id),
    (trade_depart_id),
     null)*/