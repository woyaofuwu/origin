select a.merch_spec_code,
       a.inst_id,
       a.trade_id,
       b.order_id,
       b.cust_id,
       b.cust_name,
       b.trade_eparchy_code,
       b.trade_city_code,
       b.trade_depart_id,
       b.trade_staff_id
  from tf_b_trade_grp_merch a, tf_b_trade b
 where a.trade_id = b.trade_id
   and a.ACCEPT_MONTH = b.accept_month
   and MERCH_ORDER_ID = :MERCH_ORDER_ID
   and sysdate between a.start_date and a.end_date
   and rownum = 1