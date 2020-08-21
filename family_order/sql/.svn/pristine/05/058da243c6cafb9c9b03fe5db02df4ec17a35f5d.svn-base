SELECT b.product_id para_code1, ( select v1.cust_name from tf_f_customer v1, tf_f_user v2 where v2.user_id = a.user_id_a and v1.cust_id = v2.cust_id ) para_code2, b.serial_number para_code3, ( select cust_name from tf_f_customer where cust_id = b.cust_id ) para_code4, b.rsrv_str1 para_code5, to_char( a.start_date,'yyyy-mm-dd hh24:mi:ss') para_code6, to_char( a.end_date ,'yyyy-mm-dd hh24:mi:ss') para_code7, '' para_code8, '' para_code9, '' para_code10, '' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15, '' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20, '' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25, '' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30, '' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id, '' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name  
FROM tf_f_relation_uu a, tf_f_user b
where relation_type_code = '29'
      AND start_date <= sysdate AND end_date > sysdate
      AND a.serial_number_b = b.serial_number
      AND b.product_id = :PARA_CODE1
      AND (:PARA_CODE2 IS NOT NULL OR :PARA_CODE2 IS NULL)
      AND (:PARA_CODE3 IS NOT NULL OR :PARA_CODE3 IS NULL)
      AND (:PARA_CODE4 IS NOT NULL OR :PARA_CODE4 IS NULL)
      AND (:PARA_CODE5 IS NOT NULL OR :PARA_CODE5 IS NULL)
      AND (:PARA_CODE6 IS NOT NULL OR :PARA_CODE6 IS NULL)
      AND (:PARA_CODE7 IS NOT NULL OR :PARA_CODE7 IS NULL)
      AND (:PARA_CODE8 IS NOT NULL OR :PARA_CODE8 IS NULL)
      AND (:PARA_CODE9 IS NOT NULL OR :PARA_CODE9 IS NULL)
      AND (:PARA_CODE10 IS NOT NULL OR :PARA_CODE10 IS NULL)