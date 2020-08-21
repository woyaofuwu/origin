DELETE FROM tf_f_user_svcstate a
 WHERE a.user_id=TO_NUMBER(:USER_ID)
   AND a.partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND (a.end_date>sysdate
   or exists (select 1 from tf_b_trade_svcstate_bak b where b.trade_id=to_number(:TRADE_ID) and b.user_id=a.user_id and b.service_id=a.service_id and b.state_code=a.state_code and b.start_date=a.start_date))