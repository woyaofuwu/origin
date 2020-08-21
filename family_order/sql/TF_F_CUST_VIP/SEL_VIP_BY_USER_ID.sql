select count(1) COUNTNUM
  from tf_f_cust_vip
 where user_id = :USER_ID
   and remove_tag = '0'
   and vip_type_code in (0,5)
   and vip_class_id in (1,2,3,4)