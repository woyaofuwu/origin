delete from tf_f_user_validchange a
 where user_id = TO_NUMBER(:USER_ID)
   and partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
   and (end_date > sysdate and exists 
           (select 1 from tf_b_trade_validchange_bak
                    where user_id = a.user_id
                      and start_date = a.start_date
                      and trade_id = TO_NUMBER(:TRADE_ID)
                      and accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))))