delete FROM tf_f_user_grpmbmp_sub a

 where exists (select 1 from tf_b_trade_grpmbmp_sub b

                where b.trade_id = :TRADE_ID

                  and b.user_id = a.user_id

                  and b.ec_user_id = a.ec_user_id

                  and b.biz_code = a.biz_code

                  and b.start_date = a.start_date

                  and b.modify_tag = '0')