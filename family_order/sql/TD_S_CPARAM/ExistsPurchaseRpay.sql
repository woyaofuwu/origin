SELECT COUNT(1) recordcount
  FROM tf_b_trade_purchase a
 WHERE trade_id=:TRADE_ID
   AND (EXISTS(SELECT 1 FROM td_b_purchasetrade b WHERE b.purchase_attr = a.purchase_attr
   AND b.purchase_desc = a.purchase_desc AND nvl(a.rpay_deposit, 0) = nvl(b.deposit, 0)
   AND sysdate BETWEEN start_date AND end_date)
   OR EXISTS(SELECT 1 FROM tf_f_user_other WHERE user_id=:USER_ID AND rsrv_value_code='PUSP'))