SELECT RSRV_VALUE para_code1,RSRV_STR1 para_code2,
RSRV_STR2 para_code3,
RSRV_STR3 para_code4, RSRV_STR4 para_code5,
DECODE(RSRV_STR5,'0','boss服务','1','订购关系类','2','平台定购类') para_code6, RSRV_STR6 para_code7, RSRV_STR7 para_code8, RSRV_STR8 para_code9, RSRV_STR9 para_code10,
RSRV_STR10 para_code11,USER_ID para_code12,'' para_code13,'' para_code14,'' para_code15,
'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
START_DATE start_date,END_DATE end_date,'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
FROM tf_f_user_other
 WHERE user_Id = (SELECT user_id FROM tf_f_user  WHERE serial_number=:PARA_CODE1 AND remove_tag = :PARA_CODE2)   
   and  rsrv_value_code = :PARA_CODE3 
   AND :PARA_CODE4 IS NULL
   AND :PARA_CODE5 IS NULL
   AND :PARA_CODE6 IS NULL
   AND :PARA_CODE7 IS NULL
   AND :PARA_CODE8 IS NULL
   AND :PARA_CODE9 IS NULL
   AND :PARA_CODE10 IS NULL