SELECT  b.sim_type_code para_code1,
        b.capacity_type_code para_code2,
       '' para_code3 ,
       '' para_code4 ,
       '' para_code5 ,
       '' para_code6 ,
       '' para_code7,
       '' para_code8, '' para_code9, '' para_code10,
       '' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
       '' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
       '' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
       '' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
       '' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,
       '' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
  FROM tf_f_user_other a,tf_r_emptycard b
 WHERE a.rsrv_str2 = :PARA_CODE1 
 AND   a.partition_id = 1
 AND   a.user_id=1
 AND   a.rsrv_value_code='CARD'
 AND   b.empty_card_id=a.rsrv_str1