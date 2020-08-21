SELECT nvl(b.rsrv_str1,a.sp_id) para_code1,
nvl(b.biz_type,a.sp_svc_id) para_code2,
a.sp_id para_code3,
a.sp_svc_id para_code4,
decode(a.modify_tag,'A','新增','B','激活','N','暂停','P','预退订','E','退订','未知') para_code5,
'' para_code6,
'' para_code7,
'' para_code8,'' para_code9,'' para_code10,
'' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
'' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,'' subsys_code,0 param_attr,'' param_code,'' param_name
  FROM tf_b_trade_mbmp_sub a,td_m_spservice b
 where a.trade_id=to_number(:PARA_CODE1)
and b.sp_id(+)=a.sp_id
and b.sp_svc_id(+)=a.sp_svc_id
and (:PARA_CODE2 = '' OR :PARA_CODE2 IS NULL)
and (:PARA_CODE3 = '' OR :PARA_CODE3 IS NULL)
and (:PARA_CODE4 = '' OR :PARA_CODE4 IS NULL)
AND (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL)
AND (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL)
AND (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL)
AND (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)
AND (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
AND (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)