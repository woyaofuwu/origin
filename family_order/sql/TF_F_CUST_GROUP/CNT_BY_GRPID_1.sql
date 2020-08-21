SELECT count(*)
  FROM tf_f_user u,tf_f_cust_group g,td_b_product p, td_s_brand b
 WHERE u.cust_id = g.cust_id AND u.product_id = p.product_id and p.brand_code = b.brand_code
   AND group_id =:GROUP_ID
   AND u.remove_tag = '0'