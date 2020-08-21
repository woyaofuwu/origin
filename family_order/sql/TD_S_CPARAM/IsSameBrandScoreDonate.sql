SELECT count(1) recordcount
  FROM tf_f_user_brandchange a
 WHERE user_id IN (
                  SELECT user_id
                    FROM  tf_b_trade_score
                   WHERE trade_id = TO_NUMBER(:TRADE_ID)
                     AND score_changed > 0)
                     AND end_date > SYSDATE
              AND EXISTS (SELECT 1 FROM tf_f_user_brandchange
                    WHERE user_id IN (SELECT user_id
                     FROM tf_b_trade_score
                    WHERE trade_id = TO_NUMBER(:TRADE_ID)
                      AND score_changed < 0)
                      AND end_date > SYSDATE
                      AND rsrv_str1 = a.rsrv_str1)