SELECT
booking_id para_code1,
base_line para_code2,
dead_line para_code3,
cust_name para_code4,
decode(pspt_type_code,'00','身份证','01','VIP卡','其他') para_code5,
pspt_number para_code6, org_staff_name para_code7, org_staff_id para_code8, org_staff_mobile para_code9, org_staff_phone para_code10,
org_staff_fax para_code11,org_staff_email para_code12,decode(need_destroy,'0','是','1','否','未知') para_code13,decode(need_spec_num,'1','是','0','否','未知') para_code14,decode(need_move_score,'1','是','0','否','未知') para_code15,
org_serial_number para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
'' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
  FROM tf_f_spanopen
 WHERE subscribe_state = '0'  AND  subscribe_type = '2'
   AND (:PARA_CODE1 = '' OR :PARA_CODE1 IS NULL or booking_id = :PARA_CODE1)
   AND (:PARA_CODE2 = '' OR :PARA_CODE2 IS NULL)
   AND (:PARA_CODE3 = '' OR :PARA_CODE3 IS NULL)
   AND (:PARA_CODE4 = '' OR :PARA_CODE4 IS NULL)
   AND (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL)
   AND (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL)
   AND (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL)
   AND (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)
   AND (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
   AND (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)