select trade_id
  from tf_b_trade
 where 1 = 1
   and trade_id = :trade_id
   and not exists (select 1
          from tf_f_user a
         where a.cust_id = cust_id
           and a.product_id = product_id
           and a.remove_tag = '0')