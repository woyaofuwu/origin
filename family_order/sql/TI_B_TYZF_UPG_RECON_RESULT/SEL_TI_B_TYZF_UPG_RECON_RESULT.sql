select t.recon_id,
       t.direction,
       t.order_no,
       t.buyer_id,
       t.order_money,
       t.payment,
       t.gift,
       t.req_channel,
       t.payment_type,
       t.id_type,
       t.id_value,
       t.product_id,
       t.productname,
       t.customparam,
       t.diff_type,
       t.settle_date,
       t.is_refund,
       t.orisettle_date,
       t.area_code,
       t.business_hall_code,
       t.business_hall_window,
       t.terminal_code,
       t.clerk_code,
       t.result
  from TI_B_TYZF_UPG_RECON_RESULT t
 where to_char(to_date(SETTLE_DATE, 'yyyy-mm-dd'), 'yyyy-mm-dd') =:SETTLEDATE