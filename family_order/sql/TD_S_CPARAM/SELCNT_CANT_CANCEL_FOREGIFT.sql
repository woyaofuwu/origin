SELECT COUNT(1) recordcount
  FROM tf_b_tradefee_sub a
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND fee_mode = '1' --押金
   AND fee > 0
   AND NOT EXISTS (SELECT 1 FROM tf_f_user_foregift
                    WHERE user_id = TO_NUMBER(:USER_ID)
                      AND foregift_code = a.fee_type_code
                      AND money >= a.fee)