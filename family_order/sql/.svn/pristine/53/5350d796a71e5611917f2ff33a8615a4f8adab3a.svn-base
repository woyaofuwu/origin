DELETE FROM tf_f_user_otherserv a
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND rsrv_str6 = :TRADE_ID