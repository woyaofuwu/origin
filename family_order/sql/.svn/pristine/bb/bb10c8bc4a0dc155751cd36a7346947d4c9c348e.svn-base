UPDATE tf_f_user a
      SET (user_state_codeset,last_stop_time) =
          (SELECT user_state_codeset,last_stop_time
             FROM tf_b_trade_user_bak
            WHERE trade_id = :TRADE_ID
              AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
              AND user_id = a.user_id)
    WHERE user_id = TO_NUMBER(:USER_ID)
      AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)