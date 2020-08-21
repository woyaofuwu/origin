SELECT count(1)-1 recordcount
  FROM tf_b_trade a
 WHERE a.user_id = :USER_ID
   AND a.trade_type_code in (120,150,110)
   AND a.subscribe_type<>'600'
   AND a.exec_time<=sysdate+5/24/60/60