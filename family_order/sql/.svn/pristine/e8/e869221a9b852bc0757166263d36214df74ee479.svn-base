SELECT decode(trash_mod,'0','自动处理','人工处理') para_code1,
update_staff_id para_code2,update_depart_id para_code3,update_time para_code4,'' para_code5,
'' para_code6,'' para_code7,'' para_code8,'' para_code9,'' para_code10,
'' para_code11,'' para_code12,'' para_code13,'' para_code14,'' para_code15,
'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
null start_date, null end_date,
'' eparchy_code,'' remark,''update_staff_id,''update_depart_id,''update_time,
null subsys_code, 1111 param_attr, null param_code, null param_name
FROM tl_m_trashlog 
 WHERE (:PARA_CODE1 = '' OR :PARA_CODE1 IS NULL)
   AND (:PARA_CODE2 = '' OR :PARA_CODE2 IS NULL)
   AND (:PARA_CODE3 = '' OR :PARA_CODE3 IS NULL)
   AND (:PARA_CODE4 = '' OR :PARA_CODE4 IS NULL)
   AND (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL)
   AND (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL)
   AND (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL)
   AND (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)
   AND (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
   AND (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)
   AND ROWNUM <= 1000
   order by para_code4 desc