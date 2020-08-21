delete from tf_f_user_other a
 where a.partition_id = mod(:USER_ID, 10000)
   and a.user_id = :USER_ID
   and a.trade_id = :TRADE_ID
   and a.end_date>sysdate