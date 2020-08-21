select  TO_CHAR(a.trade_id) trade_id,
        a.accept_month,
        a.user_id,
        a.merch_spec_code,
        a.product_spec_code,
        a.product_order_id,
        a.product_offer_id,
        a.group_id,
        a.serv_code,
        a.biz_attr,
        a.status,
        a.start_date,       
        a.end_date,
        a.modify_tag,
        a.update_staff_id,
        a.update_depart_id,
        a.remark        
   FROM tf_b_trade_grp_merchp a
WHERE a.trade_id = TO_NUMBER(:TRADE_ID)
  AND a.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
  and a.user_id=:USER_ID
  AND (a.MODIFY_TAG=:MODIFY_TAG OR :MODIFY_TAG IS NULL)
