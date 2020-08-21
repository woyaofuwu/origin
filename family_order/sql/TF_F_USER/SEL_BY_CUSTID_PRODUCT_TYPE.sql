select distinct up.product_id
  from tf_f_user u, tf_f_user_product up
 where 1 = 1
   and u.user_id = up.user_id
   and up.main_tag = '1'
   AND u.PARTITION_ID = up.PARTITION_ID
   and SYSDATE BETWEEN up.START_DATE AND up.END_DATE
   and u.remove_tag = '0'
   and u.cust_id = :CUST_ID