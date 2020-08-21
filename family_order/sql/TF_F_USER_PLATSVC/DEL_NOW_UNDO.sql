DELETE FROM tf_f_user_platsvc a
 WHERE a.user_id = TO_NUMBER(:USER_ID)
   AND a.partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND end_date>sysdate
   AND exists (select 1 from tf_b_trade_platsvc_bak b where b.user_id=a.user_id and b.service_id=a.service_id and b.start_date=a.start_date and b.trade_id=TO_NUMBER(:TRADE_ID))