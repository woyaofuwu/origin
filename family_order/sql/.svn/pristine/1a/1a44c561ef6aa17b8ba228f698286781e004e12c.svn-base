--IS_CACHE=Y
SELECT a.sp_id para_code1,a.sp_name para_code2,b.biz_type_code para_code3,
  b.biz_desc para_code4,b.prov_addr para_code5,b.biz_type para_code6,
  a.sp_svc_id para_code7,a.cs_tel para_code8, '' para_code9, '' para_code10,
  '' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
  '' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
  '' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
  '' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
  '' start_date,'' end_date,'' eparchy_code,'' remark,'' update_staff_id,
  '' update_depart_id,'' update_time,'' subsys_code,0 param_attr,
  '' param_code,'' param_name
FROM TD_M_SPFACTORY a,Td_m_Spservice b
WHERE a.sp_id= b.sp_id
  AND ((:PARA_CODE1 is not NULL and a.sp_id = :PARA_CODE1)
      or (:PARA_CODE1 is NULL))
  AND ((:PARA_CODE2 is not NULL and a.sp_svc_id = :PARA_CODE2)
      or (:PARA_CODE2 is NULL))
  AND ((:PARA_CODE3 is not NULL and a.sp_name LIKE '%'||:PARA_CODE3||'%')
      or (:PARA_CODE3 is NULL))
   AND (:PARA_CODE4 = '' OR :PARA_CODE4 IS NULL)
   AND (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL)
   AND (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL)
   AND (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL)
   AND (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)
   AND (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
   AND (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)