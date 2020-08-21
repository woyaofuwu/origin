UPDATE tf_f_user_svc
   SET end_date = TRUNC(LAST_DAY(SYSDATE))+1-1/24/3600
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND inst_id in (select inst_id from tf_b_trade_svc
          where trade_id = :TRADE_ID )