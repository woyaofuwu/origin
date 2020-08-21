select a.inst_id,
       TO_CHAR(a.trade_id) trade_id,
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
 WHERE 1 = 1
   and a.product_order_id = :PRODUCT_ORDER_ID
   and sysdate between a.start_date and a.end_date