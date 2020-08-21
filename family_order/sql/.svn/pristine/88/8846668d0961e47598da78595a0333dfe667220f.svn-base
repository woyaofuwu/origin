SELECT COUNT(*) ROW_NUM FROM tf_bh_trade t 
WHERE t.user_id=:USER_ID
AND t.trade_type_code='497'
AND t.accept_month = TO_NUMBER(TO_CHAR(SYSDATE,'MM'))
and to_char(t.accept_date,'yyyymmdd') >= to_char(TRUNC(SYSDATE,'MM'),'yyyymmdd')