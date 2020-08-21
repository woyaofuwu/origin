SELECT TO_CHAR(a.trade_id) trade_id,
       a.accept_month,
       TO_CHAR(a.order_id) order_id,  
       a.bpm_id,
       a.trade_type_code,
       a.priority,
       a.subscribe_type,
       a.subscribe_state,
       a.in_mode_code,
       TO_CHAR(a.cust_id) cust_id,
       a.cust_name,
       TO_CHAR(a.user_id) user_id,
       TO_CHAR(a.acct_id) acct_id,
       a.serial_number,
       a.net_type_code,
       a.eparchy_code,
       a.city_code,
       a.product_id,
       a.brand_code,
      TO_CHAR(a.accept_date,'YYYY-MM-DD HH24:MI:SS') accept_date,
       TO_CHAR(a.finish_date,'YYYY-MM-DD HH24:MI:SS') finish_date,
       TO_CHAR(a.exec_time,'YYYY-MM-DD HH24:MI:SS') exec_time,
       a.exec_action,
       a.exec_result,
       a.exec_desc,
       a.attach,
       a.ret_attach,
       a.ret_remark,
       remark,
       a.rsrv_str1,
       a.rsrv_str2,
       a.rsrv_str3,
       a.rsrv_str4,
       a.rsrv_str5,
       a.rsrv_str6,
       a.rsrv_str7,
       a.rsrv_str8,
       a.rsrv_str9,
       a.rsrv_str10,
       a.group_id
  FROM tf_b_pretrade a
 WHERE 1=1
 AND (a.trade_type_code = :TRADE_TYPE_CODE OR :TRADE_TYPE_CODE IS NULL)
 AND (a.serial_number = :SERIAL_NUMBER OR :SERIAL_NUMBER IS NULL)
 AND a.accept_date >= TRUNC(TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'))
 AND (a.accept_date < TRUNC(TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'))+1 OR :END_DATE IS NULL)
 AND (a.TRADE_ID = :TRADE_ID OR :TRADE_ID IS NULL)
 AND (accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2)) OR :TRADE_ID IS NULL)
 ORDER BY accept_date