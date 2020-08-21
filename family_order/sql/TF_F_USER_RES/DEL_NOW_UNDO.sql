DELETE FROM tf_f_user_res a
 WHERE a.user_id=TO_NUMBER(:USER_ID)
   AND a.partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND (a.end_date>sysdate
   OR EXISTS (select 1 from tf_b_trade_res_bak b where b.user_id=a.user_id and b.res_type_code=a.res_type_code and b.res_code=a.res_code and b.start_date = a.start_date and b.trade_id=:TRADE_ID))