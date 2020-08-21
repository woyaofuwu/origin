DELETE FROM tf_f_user_purchase a
 WHERE a.user_id = TO_NUMBER(:USER_ID)
   AND (end_date>sysdate
   or exists (select 1 from tf_b_trade_purchase_bak b where b.user_id=a.user_id and b.purchase_attr=a.purchase_attr and b.process_tag=a.process_tag and b.start_date=a.start_date and b.trade_id=to_number(:TRADE_ID)))
   AND process_tag = '0'