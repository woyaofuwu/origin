SELECT b.serial_number_a para_code1,
      d.cust_name para_code2,
      b.serial_number_b para_code3,
      c.state_name para_code4,
      a.open_date para_code5,
      d.remark para_code6,
       '' para_code7,
       '' para_code8, '' para_code9, '' para_code10,
       '' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
       '' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
       '' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
       '' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
       '' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,
       '' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
  FROM tf_f_user a,tf_f_relation_uu b,td_s_servicestate c,tf_f_customer d
 WHERE a.remove_tag='0'
   AND a.User_Id=b.user_id_b
   AND a.partition_id=b.partition_id
   AND b.User_Id_a = (SELECT user_id FROM tf_f_user WHERE serial_number=:PARA_CODE1 AND remove_tag='0')
   AND c.service_id=75
   AND c.state_code(+)=a.user_state_codeset
   AND d.cust_id=a.cust_id
   AND b.relation_type_code='51'
   AND (:PARA_CODE2 IS NULL OR :PARA_CODE2='')
   AND (:PARA_CODE3 IS NULL OR :PARA_CODE3='')
   AND (:PARA_CODE4 IS NULL OR :PARA_CODE4='')
   AND (:PARA_CODE5 IS NULL OR :PARA_CODE5='')
   AND (:PARA_CODE6 IS NULL OR :PARA_CODE6='')
   AND (:PARA_CODE7 IS NULL OR :PARA_CODE7='')
   AND (:PARA_CODE8 IS NULL OR :PARA_CODE8='')
   AND (:PARA_CODE9 IS NULL OR :PARA_CODE9='')
   AND (:PARA_CODE10 IS NULL OR :PARA_CODE10='')