SELECT count(1) recordcount
  FROM tf_b_trade_discnt a
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month=TO_NUMBER(:ACCEPT_MONTH)
   AND exists(select 1 from td_b_prod_discnt_member b,td_b_product c where b.discnt_code=a.discnt_code and b.product_id =c.product_id
                and c.brand_code=:BRAND_CODE)
   AND modify_tag=:MODIFY_TAG