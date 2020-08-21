SELECT b.vpn_no para_code1,to_char(a.user_id) para_code2,b.vpn_name para_code3,a.out_group_id para_code4,
       a.serial_number para_code5,a.short_code para_code6,
        '' para_code7, '' para_code8, '' para_code9, '' para_code10,
        '' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
        '' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
        '' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
        '' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,       
        to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,
        '' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
 FROM tf_f_vpmn_grpout a, tf_f_user_vpn b
WHERE a.user_id = b.user_id
  AND sysdate BETWEEN a.start_date AND a.end_date
  AND EXISTS (SELECT 1 FROM tf_f_vpmn_grpout
                WHERE user_id = a.user_id
                  AND serial_number = :PARA_CODE1
                  AND sysdate BETWEEN start_date AND end_date)
  AND (:PARA_CODE2 IS NOT NULL OR :PARA_CODE2 IS NULL)
  AND (:PARA_CODE3 IS NOT NULL OR :PARA_CODE3 IS NULL)
  AND (:PARA_CODE4 IS NOT NULL OR :PARA_CODE4 IS NULL)
  AND (:PARA_CODE5 IS NOT NULL OR :PARA_CODE5 IS NULL)
  AND (:PARA_CODE6 IS NOT NULL OR :PARA_CODE6 IS NULL)
  AND (:PARA_CODE7 IS NOT NULL OR :PARA_CODE7 IS NULL)
  AND (:PARA_CODE8 IS NOT NULL OR :PARA_CODE8 IS NULL)
  AND (:PARA_CODE9 IS NOT NULL OR :PARA_CODE9 IS NULL)
  AND (:PARA_CODE10 IS NOT NULL OR :PARA_CODE10 IS NULL)