SELECT to_char(a.user_id_a) user_id_a,
       a.serial_number_a,
       a.user_id_b,
       to_char(a.user_id_b) user_id_b,
       a.serial_number_b,
       a.relation_type_code,
       a.role_type_code,
       a.role_code_a,
       a.role_code_b,
       a.orderno,
       a.short_code,
       a.inst_id,
       to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date ,
       b.cust_id,
       b.brand_code,
       b.product_id,
       b.net_type_code,
       b.remove_tag
  FROM TF_F_RELATION_UU a, tf_f_user b
 WHERE 1 = 1
   AND user_id_a = TO_NUMBER(:USER_ID_A)
   AND role_code_b = :ROLE_CODE_B
   AND relation_type_code = :RELATION_TYPE_CODE
   AND sysdate BETWEEN a.start_date AND a.end_date
   and b.user_id = a.user_id_b