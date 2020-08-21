select up.product_id, u.serial_number, u.user_id user_id_a 
  from tf_f_user u, tf_f_user_product up 
  where u.user_id = up.user_id and up.brand_code = 'DLBG' and u.remove_tag = '0' and u.cust_id = :CUST_ID 