select count(1) recordcount from dual where
(SELECT nvl(SUM(score_value),0) recordcount FROM tf_f_user a
   WHERE user_id=to_number(:USER_ID)
     AND partition_id=MOD(to_number(:USER_ID), 10000)
    ) >= to_number(:NUM)  or
 (select count(1) recordcount from tf_bh_trade where
  accept_date>=to_date('20070401','yyyymmdd') and user_id=to_number(:USER_ID)
 and trade_type_code=330)>0