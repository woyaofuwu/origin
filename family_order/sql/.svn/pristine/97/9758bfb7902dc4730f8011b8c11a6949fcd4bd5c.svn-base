SELECT COUNT(1) recordcount
 FROM   td_b_attr_itemb a
 WHERE  a.id_type = 'S'
 and    a.id = :SERVICE_ID
 AND    a.rsrv_str4 = :RSRV_STR4
 AND    EXISTS (SELECT 1
        FROM   tf_b_trade_attr b
        WHERE  b.trade_id = :TRADE_ID
        and    b.accept_month = to_number(substr(:TRADE_ID, 5,2))
        AND    b.rsrv_num1 = a.id
        AND    b.modify_tag = :MODIFY_TAG
        AND    b.attr_value = a.attr_field_code)