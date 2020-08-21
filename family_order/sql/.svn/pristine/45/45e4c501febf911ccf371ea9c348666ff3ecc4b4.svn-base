SELECT a.refer_id para_code1,a.refer_type para_code2,a.user_level para_code3,
a.refer_info para_code4,a.assign_info para_code5,a.dispose_info para_code6,
a.assign_staff_id para_code7,a.assign_depart_id para_code8,a.assign_depart_name para_code9,
'' para_code10,'' para_code11 ,'' para_code12,'' para_code13,
a.dispose_staff_id para_code14,a.dispose_depart_id para_code15,a.dispose_depart_name para_code16,
a.dispose_tache_info para_code17,a.rsrv_str1 para_code18,a.rsrv_str2 para_code19,a.rsrv_str3 para_code20,
to_char(a.accept_date,'yyyy-mm-dd hh24:mi:ss') para_code21,to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') para_code22,
to_char(a.dispose_date,'yyyy-mm-dd hh24:mi:ss') para_code23,to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') para_code24,
to_char(a.rsrv_date1,'yyyy-mm-dd hh24:mi:ss') para_code25,
to_char(a.rsrv_date2,'yyyy-mm-dd hh24:mi:ss') para_code26,a.trade_staff_id para_code27,
a.trade_staff_phone para_code28,a.trade_staff_name para_code29,'' para_code30,'' start_date,'' end_date,'' eparchy_code,
a.remark remark,a.update_staff_id update_staff_id,a.update_depart_id update_depart_id,
to_char(a.update_time,'yyyy-mm-dd hh24:mi:ss') update_time,'' subsys_code,
0 param_attr,'' param_code,'' param_name
  FROM TF_F_USER_REFER a
 WHERE a.serial_number = :PARA_CODE1
   AND a.accept_date BETWEEN TO_DATE(:PARA_CODE2, 'yyyy-mm-dd hh24:mi:ss') AND TO_DATE(:PARA_CODE3,     'yyyy-mm-dd hh24:mi:ss')
   AND (:PARA_CODE4 = '' OR :PARA_CODE4 IS NULL)
   AND (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL)
   AND (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL)
   AND (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL)
   AND (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)
   AND (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
   AND (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)