SELECT b.serial_number_a para_code1, (select c.cust_name from tf_f_customer c where c.partition_id=mod(a.cust_id,10000) and c.cust_id = a.cust_id  ) para_code2,( SELECT serv_para2 FROM tf_f_user_svc where partition_id=mod(b.user_id_b,10000) and user_id = b.user_id_b and service_id=1160  and start_date <= sysdate and end_date > sysdate ) para_code3,b.serial_number_b  para_code4,to_char( b.start_date, 'yyyy-mm-dd hh24:mi:ss' ) para_code5,to_char( b.end_date, 'yyyy-mm-dd hh24:mi:ss' )   para_code6,'' para_code7, '' para_code8, '' para_code9, '' para_code10, '' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15, '' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20, '' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25, '' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30, '' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id, '' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
FROM  tf_f_user a, tf_f_relation_uu b
WHERE b.start_date >= to_date(:PARA_CODE1,'yyyy-mm-dd hh24:mi:ss')
      AND b.start_date <= to_date(:PARA_CODE2,'yyyy-mm-dd hh24:mi:ss')
      AND b.start_date <= sysdate and b.end_date > sysdate
      AND b.relation_type_code = '28'
      AND b.user_id_a =to_number( :PARA_CODE3)
      AND a.user_id = b.user_id_a 
      AND a.partition_id=mod(b.user_id_a,10000)
      --AND (b.serial_number_a = :PARA_CODE4 OR :PARA_CODE4 IS NULL)
      --AND (:PARA_CODE3 IS NOT NULL OR :PARA_CODE3 IS NULL)
      --AND ( a.serial_number = :PARA_CODE4 OR :PARA_CODE4 IS NULL )
      AND (:PARA_CODE4 IS NOT NULL OR :PARA_CODE4 IS NULL)
      AND (:PARA_CODE5 IS NOT NULL OR :PARA_CODE5 IS NULL)
      AND (:PARA_CODE6 IS NOT NULL OR :PARA_CODE6 IS NULL)
      AND (:PARA_CODE7 IS NOT NULL OR :PARA_CODE7 IS NULL)
      AND (:PARA_CODE8 IS NOT NULL OR :PARA_CODE8 IS NULL)
      AND (:PARA_CODE9 IS NOT NULL OR :PARA_CODE9 IS NULL)
      AND (:PARA_CODE10 IS NOT NULL OR :PARA_CODE10 IS NULL)