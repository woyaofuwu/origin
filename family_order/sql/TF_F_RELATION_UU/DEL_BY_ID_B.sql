DELETE FROM tf_f_relation_uu
 WHERE user_id_b in (
 select id_b from tf_b_trade_relation
 where  trade_id = :TRADE_ID
 and    modify_tag = '1')