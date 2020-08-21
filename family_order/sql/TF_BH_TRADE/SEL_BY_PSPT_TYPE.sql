select a.trade_staff_id, count(*) total
  from tf_bh_trade a, tf_b_trade_customer b
 where a.trade_id = b.trade_id
   and a.trade_type_code = '10'
   and b.pspt_TYPE_CODE = 'Z'
   and to_char(a.accept_date, 'YYYY-MM-DD') = :DATE
   and a.accept_month = to_number(to_char(a.accept_date, 'MM'))
   and a.cancel_tag = '0'
 group by a.trade_staff_id
 having count(*) > :VALUE