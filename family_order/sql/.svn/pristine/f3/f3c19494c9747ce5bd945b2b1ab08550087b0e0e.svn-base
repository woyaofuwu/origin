delete FROM tf_f_user_grpmbmp_info a
 where exists (select 1 from tf_b_trade_grpmbmp_info b
                where b.trade_id = :TRADE_ID
                  and b.user_id = a.user_id
                  and b.biz_code = a.biz_code
                  and b.start_date = a.start_date
                  and b.oper_code <> '70')