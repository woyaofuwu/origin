--IS_CACHE=Y
select product_id, product_name
  from TD_B_PRODUCT t
 where 1 = 1
   and t.brand_code = :BRAND_CODE
   and t.product_state = 4
   and t.end_date > sysdate
 order by product_name, product_id