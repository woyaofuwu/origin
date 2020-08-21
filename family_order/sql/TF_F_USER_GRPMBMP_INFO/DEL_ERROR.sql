DELETE FROM tf_f_user_grpmbmp_info a
 WHERE exists (select 1 from tf_b_trade_grpmbmp_info b 
                where b.trade_id = :TRADE_ID
                  and b.user_id = a.user_id)
   and a.start_date >= a.end_date