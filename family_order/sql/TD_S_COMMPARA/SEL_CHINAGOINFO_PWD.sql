SELECT (SELECT a.param_value
FROM ti_c_olcomcommonquery a,ti_c_olcomwork b
WHERE a.olcom_work_id =  b.olcom_work_id
AND b.trade_id = TO_NUMBER(:PARA_CODE1)
AND a.param_code = 'USERPWD') para_code1,
'' para_code2,
'' para_code3,
(SELECT a.olcom_state
FROM ti_c_olcomwork a
WHERE a.trade_id = TO_NUMBER(:PARA_CODE1)
UNION ALL
SELECT a.olcom_state
FROM ti_ch_olcomwork a
WHERE a.trade_id = TO_NUMBER(:PARA_CODE1)) para_code15,
'' para_code4,
'' para_code5,
'' para_code6,
'' para_code7,
'' para_code8,'' para_code9,'' para_code10,'' para_code11,'' para_code12,'' para_code13,'' para_code14,		
'' para_code16,'' para_code17,'' para_code18,'' para_code19,'' para_code20,
'' para_code21,'' para_code22,'' para_code23,'' para_code24,'' para_code25,
'' para_code26,'' para_code27,'' para_code28,'' para_code29,'' para_code30,
'' start_date,''end_date,
'' eparchy_code,'' remark,'' update_staff_id,'' update_depart_id,'' update_time,
'' subsys_code,0 param_attr,'' param_code,'' param_name
FROM dual
WHERE (:PARA_CODE2 = '' OR :PARA_CODE2 IS NULL)
AND (:PARA_CODE3 = '' OR :PARA_CODE3 IS NULL)
AND (:PARA_CODE4 = '' OR :PARA_CODE4 IS NULL)
AND (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL)
AND (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL)
AND (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL)
AND (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)
AND (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
AND (:PARA_CODE10 = '' OR :PARA_CODE10  IS NULL)