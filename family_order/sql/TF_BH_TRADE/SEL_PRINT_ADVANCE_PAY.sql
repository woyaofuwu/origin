select t.user_id,
       t.serial_number,
       t.cust_name,
       t.trade_id,
       t.order_id,
       t.trade_type_code,
       t.accept_date,
       '预存款' as fee_mode_name,
       '2' as fee_mode,
       t.advance_pay as fee,
       t.trade_staff_id,
       t.trade_depart_id,
       t.trade_eparchy_code
  from tf_b_trade t
 where 1 = 1
   and t.trade_id = :TRADE_ID
   and t.serial_number = :SERIAL_NUMBER
   and t.accept_month = to_number(:ACCEPT_MONTH)
   and to_char(t.accept_date, 'yyyy-mm') = :ACCEPT_TIME
   and t.trade_staff_id = :TRADE_STAFF_ID
   and t.advance_pay > 0 --预存款
   and t.cancel_tag = '0'
   and t.accept_date > to_date('2014-09-01','yyyy-mm-dd')
union
select t.user_id,
       t.serial_number,
       t.cust_name,
       t.trade_id,
       t.order_id,
       t.trade_type_code,
       t.accept_date,
       '预存款' as fee_mode_name,
       '2' as fee_mode,
       t.advance_pay as fee,
       t.trade_staff_id,
       t.trade_depart_id,
       t.trade_eparchy_code
  from tf_bh_trade t
 where 1 = 1
   and t.trade_id = :TRADE_ID
   and t.serial_number = :SERIAL_NUMBER
   and t.accept_month = to_number(:ACCEPT_MONTH)
   and to_char(t.accept_date, 'yyyy-mm') = :ACCEPT_TIME
   and t.trade_staff_id = :TRADE_STAFF_ID
   and t.advance_pay > 0 ----预存款
   and t.cancel_tag = '0'
   and t.accept_date > to_date('2014-09-01','yyyy-mm-dd')