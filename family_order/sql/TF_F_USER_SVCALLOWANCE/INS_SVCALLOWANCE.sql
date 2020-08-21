INSERT INTO tf_f_user_svcallowance(partition_id,user_id,service_type,deal_flag,start_date,end_date,update_date)
select mod(TO_NUMBER(a.user_id), 10000),a.user_id,a.info_code,decode(a.info_value,'0000','0','1111','1','0'),sysdate,
TO_DATE('2050-12-31 23:59:59','YYYY-MM-DD HH24:MI:SS'),sysdate
 from tf_b_trade_mbmp_plus a
WHERE a.trade_id = TO_NUMBER(:TRADE_ID)