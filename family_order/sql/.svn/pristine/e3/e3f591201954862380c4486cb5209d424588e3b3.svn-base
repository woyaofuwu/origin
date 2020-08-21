UPDATE tf_f_relation_uu
    SET end_date = sysdate
  WHERE user_id_b in (select USER_ID_B
                        from tf_b_trade_relation
                       where trade_id = :TRADE_ID AND MODIFY_TAG = :MODIFY_TAG)
  AND RELATION_TYPE_CODE in (:RELATION_TYPE_CODE1,:RELATION_TYPE_CODE2)
  AND end_date > SYSDATE