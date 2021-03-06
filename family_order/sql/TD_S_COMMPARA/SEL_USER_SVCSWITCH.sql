SELECT a.service_type para_code1,a.deal_flag para_code2,
to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') para_code3,
to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') para_code4, '' para_code5,
'' para_code6, '' para_code7, '' para_code8, '' para_code9, '' para_code10,
'' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
'' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
  FROM tf_f_user_svcswitch a
 WHERE a.user_id=TO_NUMBER(:PARA_CODE1)
    AND a.partition_id=MOD(TO_NUMBER(:PARA_CODE1),10000)
    AND sysdate between start_date and end_date
    AND (a.service_type=:PARA_CODE2 OR :PARA_CODE2 = '' OR :PARA_CODE2 IS NULL)
    AND (a.deal_flag=:PARA_CODE3 OR :PARA_CODE3 = '' OR :PARA_CODE3 IS NULL)