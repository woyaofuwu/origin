SELECT c.cust_name para_code1,to_char(a.serial_number) para_code2,a.rsrv_str1 para_code3,a.rsrv_str2 para_code4,a.rsrv_str3 para_code5,a.rsrv_str4 para_code6,a.rsrv_str5 para_code7,a.rsrv_str6 para_code8,a.rsrv_str7 para_code9,
       a.rsrv_str8 para_code10,a.open_date para_code11 ,a.product_id para_code12,a.user_id  para_code13,'' para_code14,'' para_code15,
'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
'' start_date,''end_date,
'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,
'' subsys_code,0 param_attr,'' param_code,'' param_name
  FROM tf_f_user a,tf_f_relation_uu b,tf_f_cust_group c
 WHERE b.User_Id_b = :PARA_CODE1
   AND b.relation_type_code = :PARA_CODE2
   AND b.end_date > SYSDATE
   AND a.User_Id = b.user_id_a
   AND a.remove_tag = '0'
   AND c.cust_id = a.cust_id
   AND c.remove_tag = '0'
   AND (:PARA_CODE3 = '' OR :PARA_CODE3 IS NULL)
   AND (:PARA_CODE4 = '' OR :PARA_CODE4 IS NULL)
   AND (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL)
   AND (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL)
   AND (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL)
   AND (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)
   AND (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
   AND (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)