--IS_CACHE=Y
select b.discnt_code paracode, b.discnt_name paraname
  from td_b_discnt b,td_b_product_discnt c
 where b.discnt_code = c.discnt_code
   and b.discnt_type_code = 'R'
   and c.product_id = 10001005
   and sysdate between b.start_date and b.end_date
   and sysdate between c.start_date and c.end_date
   and (:TRADE_EPARCHY_CODE is null or :TRADE_EPARCHY_CODE is not null)