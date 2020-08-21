SELECT to_char(a.trade_id) trade_id,to_number(ACCEPT_MONTH) ACCEPT_MONTH,a.user_id user_id,a.sale_project_id sale_project_id,to_char(SIGN_UP,'yyyy-mm-dd hh24:mi:ss') SIGN_UP,a.start_date start_date,a.end_date end_date,b.min_number rsrv_str1,b.max_number rsrv_str2,b.sale_project rsrv_str3,'' RSRV_STR4,'' RSRV_STR5,'' MODIFY_TAG
  FROM tf_b_trade_saleproject a,td_b_sale_project b
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND user_id = TO_NUMBER(:USER_ID)
   AND modify_tag = '0'
   AND SYSDATE < a.end_date
   AND a.sale_project_id = b.sale_project_id
   AND SYSDATE < b.end_date
UNION ALL
SELECT to_char(0) trade_id,to_number(0) ACCEPT_MONTH,c.user_id user_id,c.sale_project_id sale_project_id,to_char('','yyyy-mm-dd hh24:mi:ss') SIGN_UP,c.start_date start_date,c.end_date end_date,d.min_number rsrv_str1,d.max_number rsrv_str2,d.sale_project rsrv_str3,'' RSRV_STR4,'' RSRV_STR5,'' MODIFY_TAG
  FROM tf_f_user_saleproject c,td_b_sale_project d
 WHERE partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id = TO_NUMBER(:USER_ID)
   AND SYSDATE < c.end_date
   AND c.sale_project_id = d.sale_project_id
   AND SYSDATE < d.end_date
   AND NOT EXISTS(SELECT 1 FROM tf_b_trade_saleproject
                   WHERE trade_id = TO_NUMBER(:TRADE_ID)
                     AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
                     AND c.user_id = user_id
                     AND c.sale_project_id = sale_project_id
                     AND c.start_date = start_date
                     AND modify_tag = '1')