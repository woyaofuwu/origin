DELETE FROM tf_f_user_grpmbmp_plus a
 WHERE EXISTS(SELECT 1 FROM tf_b_trade_grpmbmp_plus
               WHERE trade_id = :TRADE_ID
                 AND user_id = a.user_id
                 AND biz_code = a.biz_code
                 AND x_tag = a.x_tag)