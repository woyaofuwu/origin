select b.vpn_no para_code1,b.vpn_name para_code2,a.serial_number_b para_code3,
       a.short_code para_code4, to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') para_code5,
        to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') para_code6,
       '' para_code7,
       '' para_code8,
       '' para_code9, to_char(b.feeindex) para_code10,
       '' para_code11,'' para_code12,'' para_code13,
       '' para_code14,'' para_code15,'' para_code16,'' para_code17,'' para_code18,
       '' para_code19,'' para_code20,'' para_code21,'' para_code22,'' para_code23,
       '' para_code24,'' para_code25,'' para_code26,'' para_code27,'' para_code28,
       '' para_code29,'' para_code30,'' start_date,'' end_date,'' eparchy_code,'' remark,
       '' update_staff_id,'' update_depart_id,'' update_time,
       '' subsys_code,0 param_attr,'' param_code,'' param_name
  from tf_f_user_vpn b , tf_f_relation_uu a
    where ( (:PARA_CODE1 = '2' AND b.vpn_name like '%'||:PARA_CODE2||'%' and a.short_code =    :PARA_CODE3)
     OR (:PARA_CODE1 = '1' AND b.vpn_no=:PARA_CODE2 and a.short_code = :PARA_CODE3 )
     OR (:PARA_CODE1 = '0' AND a.serial_number_b = :PARA_CODE2 and :PARA_CODE3 is null))
    AND b.user_id=a.user_id_a
    AND sysdate between a.start_date and a.end_date
     AND (:PARA_CODE4 = '' OR :PARA_CODE4 IS NULL)
     AND (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL)
     AND (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL)
     AND (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL)
     AND (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)
     AND (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
     AND (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)