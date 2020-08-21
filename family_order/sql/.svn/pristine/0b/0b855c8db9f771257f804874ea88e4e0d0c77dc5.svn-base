SELECT  '' TRADE_ID,'' SUBSCRIBE_ID,'' BPM_ID,-1 TRADE_TYPE_CODE,'' IN_MODE_CODE,-1 PRIORITY,
 '' SUBSCRIBE_STATE,'' NEXT_DEAL_TAG,-1 PRODUCT_ID,'' BRAND_CODE,'' USER_ID,'' CUST_ID,
 '' ACCT_ID,'' SERIAL_NUMBER,'' CUST_NAME,'' ACCEPT_DATE,-1 ACCEPT_MONTH,'' TRADE_STAFF_ID,
 '' TRADE_DEPART_ID,'' TRADE_CITY_CODE, '' TRADE_EPARCHY_CODE,'' TERM_IP,'' EPARCHY_CODE,
 '' CITY_CODE,'' OLCOM_TAG,'' EXEC_TIME,'' FINISH_DATE,'' OPER_FEE,'' FOREGIFT,'' ADVANCE_PAY,
 '' INVOICE_NO,'' FEE_STATE,'' FEE_TIME,'' FEE_STAFF_ID,'' CANCEL_TAG,'' CANCEL_DATE,
 '' CANCEL_STAFF_ID,'' CANCEL_DEPART_ID,'' CANCEL_CITY_CODE,'' CANCEL_EPARCHY_CODE,'' PROCESS_TAG_SET,
 abs(sum(b.score_changed)) RSRV_STR1, --总扣减及兑换积分
 abs(sum(decode(a.trade_type_code,330,b.score_changed,0))) RSRV_STR2, --兑换积分
 abs(sum(decode(a.trade_type_code,110,b.score_changed,0))) RSRV_STR3, --转品牌清零积分
 abs(sum(decode(a.trade_type_code,7230,b.score_changed,0))) RSRV_STR4, --销号清零积分
 abs(sum(decode(a.remark,'用户积分三年滚动清零',b.score_changed,0))) RSRV_STR5, --三年被动清零积分
 abs(sum(decode(a.trade_type_code,510,b.score_changed,0))) RSRV_STR6, --积分支付
 abs(sum(decode(a.trade_type_code,340,b.score_changed,0))) RSRV_STR7, --积分捐赠
 '' RSRV_STR8,'' RSRV_STR9,'' RSRV_STR10,'' REMARK
  FROM TF_BH_TRADE a, tf_b_trade_score b
 WHERE a.trade_id = b.trade_id
   AND a.accept_month = b.accept_month
   AND a.user_id = TO_NUMBER(:USER_ID)
   AND a.accept_date between TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS')
                         and TO_DATE(:FINISH_DATE, 'YYYY-MM-DD HH24:MI:SS')
   AND b.score_changed < 0