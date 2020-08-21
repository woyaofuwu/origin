UPDATE tf_f_user_mbmp_plus a
   SET a.info_value=(select b.info_value from tf_b_trade_mbmp_plus b where b.trade_id=to_number(:TRADE_ID) and a.user_id=b.user_id and a.info_code=b.info_code and a.biz_type_code = b.biz_type_code)
WHERE user_id=(SELECT distinct user_id from tf_b_trade_mbmp_plus c where c.trade_id=to_number(:TRADE_ID))
  AND partition_id = MOD(user_id,10000)
  AND exists (select 1 from tf_b_trade_mbmp_plus c where c.trade_id=to_number(:TRADE_ID) and a.info_code=c.info_code and a.biz_type_code = c.biz_type_code)