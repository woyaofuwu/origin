select b.*
  from TF_B_TRADE_SALE_ACTIVE_BAK b
 where b.TRADE_ID = :TRADE_ID
   and b.product_id = '69900703'
   order by b.end_date desc