select count(1) RECORDCOUNT
  from tf_b_trade t, tf_b_trade_relation r
 where r.trade_id = t.trade_id
   and t.trade_type_code in ('3034', '3035')
   and r.user_id_a =:USER_ID_A
   and r.short_code =:SHORT_CODE
   and rownum <2