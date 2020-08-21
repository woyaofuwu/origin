select count(1) recordcount
  from tf_f_user u, tf_f_cust_vip p, td_m_vipclass v
 where u.user_id = p.user_id
   and p.vip_type_code = v.vip_type_code
   and p.vip_class_id = v.class_id
   and v.vip_type_code = :VIP_TYPE_CODE
   and v.class_id =:CLASS_ID
   and u.user_id = :USER_ID
