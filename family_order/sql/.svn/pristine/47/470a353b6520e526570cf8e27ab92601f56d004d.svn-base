select um.*
  from tf_f_cust_group     c,
       tf_f_user           u,
       tf_f_user_product   up,
       tf_f_user_grp_merch um
 where c.cust_id = u.cust_id
   and c.group_id = :GROUP_ID
   and u.user_id = up.user_id
   and u.user_id = um.user_id
   and u.partition_id = up.partition_id
   and u.partition_id = um.partition_id
   and u.partition_id = mod(u.user_id, 10000)
   and up.product_id = :PRODUCT_ID
   and c.remove_tag = '0'
   and u.remove_tag = '0'
   and up.main_tag = '1'
   and (sysdate between up.start_date and up.end_date)
   and um.status = 'A'
