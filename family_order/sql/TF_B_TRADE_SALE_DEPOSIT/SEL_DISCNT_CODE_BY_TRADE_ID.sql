select a.a_discnt_code
  from tf_b_trade_sale_deposit a, tf_b_tradefee_sub b
 where a.TRADE_ID = :TRADE_ID
   and a.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID, 5, 2))
   and a.modify_tag = :MODIFY_TAG
   and a.trade_id = b.trade_id
   and a.discnt_gift_id = b.discnt_gift_id