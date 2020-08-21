SELECT count(*) recordcount
  FROM tf_f_group_order o,td_b_grp_product p
 WHERE o.prevaluec1=p.product_name
   AND (:PARAM0 IS NULL OR group_id=:PARAM0)
   AND (:PARAM1 IS NULL OR prevaluec1 in
                 ( select product_name from td_b_grp_product
		   start with product_name = :PARAM1
		   connect by prior product_name = brand_code
		  ) )