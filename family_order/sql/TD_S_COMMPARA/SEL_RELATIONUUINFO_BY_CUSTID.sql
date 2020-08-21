SELECT b.serial_number_a para_code1,
       b.serial_number_b para_code2,
       a.product_id para_code3,
       b.short_code para_code4,
       b.role_code_b para_code5,
       to_char(b.start_date, 'yyyy-mm-dd hh24:mi:ss') para_code6,
       '' para_code7,
       '' para_code8,
       '' para_code9,
       '' para_code10,
       '' para_code11,
       '' para_code12,
       '' para_code13,
       '' para_code14,
       '' para_code15,
       '' para_code16,
       '' para_code17,
       '' para_code18,
       '' para_code19,
       '' para_code20,
       '' para_code21,
       '' para_code22,
       '' para_code23,
       '' para_code24,
       '' para_code25,
       '' para_code26,
       '' para_code27,
       '' para_code28,
       '' para_code29,
       '' para_code30,
       '' start_date,
       '' end_date,
       '' eparchy_code,
       '' remark,
       '' update_staff_id,
       '' update_depart_id,
       '' update_time,
       '' subsys_code,
       0 param_attr,
       '' param_code,
       '' param_name 
  FROM tf_f_user a, tf_f_relation_uu b
 WHERE a.remove_tag = '0'
   AND a.user_id = b.user_id_a
   AND sysdate < b.end_date
   AND a.cust_id = :PARA_CODE1
   AND b.relation_type_code = :PARA_CODE2
   AND b.role_code_b = :PARA_CODE3
   AND (:PARA_CODE4 = '' OR :PARA_CODE4 IS NULL)
   AND (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL)
   AND (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL)
   AND (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL)
   AND (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)
   AND (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
   AND (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)