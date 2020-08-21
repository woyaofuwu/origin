SELECT
a.serial_number para_code1,b.param_name para_code2,
a.rsrv_str1 para_code3,
a.rent_serial_number para_code4, a.nationality_areacode para_code5,
a.rent_type_code para_code6,
a.rsrv_str2 para_code7, to_char(a.rsrv_date1,'yyyy-mm-dd hh24:mi:ss') para_code8, '' para_code9, '' para_code10,
'' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
a.start_date start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
  FROM tf_f_user_rent a,td_s_commpara b
 WHERE a.user_id = :PARA_CODE1
   AND b.subsys_code = 'CSM'
   AND b.param_attr = 743
   AND a.rent_type_code = b.param_code
   AND a.end_date > sysdate
   AND a.rsrv_str1 = :PARA_CODE2
   AND (:PARA_CODE3 = '' OR :PARA_CODE3 IS NULL)
   AND (:PARA_CODE4 = '' OR :PARA_CODE4 IS NULL)
   AND (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL)
   AND (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL)
   AND (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL)
   AND (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)
   AND (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
   AND (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)