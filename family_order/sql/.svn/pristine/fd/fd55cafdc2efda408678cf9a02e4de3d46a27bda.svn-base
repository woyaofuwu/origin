SELECT count(*)
  FROM tf_f_relation_uu  uu, 
       td_b_ptype_product pp,
       TD_S_PRODUCT_TYPE  pt, 
       tf_f_user          u
 WHERE u.product_id = pp.product_id
   AND pt.product_type_code = pp.product_type_code 
   AND u.user_id = uu.user_id_a
   AND u.remove_tag = '0'
   AND SYSDATE BETWEEN uu.start_date AND uu.end_date
   AND exists (
      select  1
        from  tf_f_user u
       where  u.serial_number = :SERIAL_NUMBER
         and  u.remove_tag = '0' 
         and  u.user_id = uu.user_id_b
         and  u.partition_id = mod(u.user_id,10000)
  )
  AND exists (
      select  1
        from  tf_f_user u,tf_f_cust_group g
       where  u.cust_id = g.cust_id  
        and  u.user_id = uu.user_id_a
         and  group_id = :GROUP_ID 
         and  g.remove_tag='0' 
         and  u.remove_tag = '0'
  )