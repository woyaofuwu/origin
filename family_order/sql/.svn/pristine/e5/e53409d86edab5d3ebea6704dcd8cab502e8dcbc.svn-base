SELECT a.user_id para_code1,partition_id para_code2,
       a.serial_number para_code3,b.feeback para_code4,in_date para_code5,
       a.start_acyc_id para_code6, a.end_acyc_id para_code7, to_char(fee/100) para_code8, process_tag para_code9, pre_value_c1 para_code10,
       pre_value_c2 para_code11,to_char(pre_value_N1) para_code12,to_char(pre_value_N2) para_code13,to_char(pre_value_D1,'yyyy-mm-dd hh24:mi:ss') para_code14,to_char(pre_value_D2,'yyyy-mm-dd hh24:mi:ss') para_code15,
       '' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
       '' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
       '' para_code26,'' para_code27,'' para_code28,'' para_code29,b.remark para_code30,
       '' start_date,'' end_date,'' eparchy_code,'' remark,
       '' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,
       0 param_attr,'' param_code,'' param_name       
  FROM tf_a_user_feeback a,td_a_presentfee b
 WHERE a.feeback_code = b.feeback_code
   AND a.user_id = TO_number(:PARA_CODE1)
   AND partition_id=MOD(TO_number(:PARA_CODE1),10000)
   AND a.process_tag ='1'
   AND in_date BETWEEN to_date(:PARA_CODE2,'YYYY-MM-DD hh24:mi:ss') AND to_date(:PARA_CODE3,'YYYY-MM-DD hh24:mi:ss')
   AND (:PARA_CODE3 IS NOT NULL OR :PARA_CODE3 IS NULL)
   AND (:PARA_CODE4 IS NOT NULL OR :PARA_CODE4 IS NULL)
   AND (:PARA_CODE5 IS NOT NULL OR :PARA_CODE5 IS NULL)
   AND (:PARA_CODE6 IS NOT NULL OR :PARA_CODE6 IS NULL)
   AND (:PARA_CODE7 IS NOT NULL OR :PARA_CODE7 IS NULL)
   AND (:PARA_CODE8 IS NOT NULL OR :PARA_CODE8 IS NULL)
   AND (:PARA_CODE9 IS NOT NULL OR :PARA_CODE9 IS NULL)
   AND (:PARA_CODE10 IS NOT NULL OR :PARA_CODE10 IS NULL)