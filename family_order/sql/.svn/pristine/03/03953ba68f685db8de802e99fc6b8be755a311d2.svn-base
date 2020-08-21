--IS_CACHE=Y
SELECT param_code, param_name, para_code1, subsys_code, param_attr,start_date, end_date, eparchy_code, remark, update_staff_id,
update_depart_id, update_time, para_code2, para_code3, para_code4, para_code5, para_code6, para_code7, para_code8, para_code9,
para_code10, para_code11, para_code12, para_code13, para_code14, para_code15, para_code16, para_code17, para_code18, para_code19,
para_code20, para_code21, para_code22, para_code23, para_code24, para_code25, para_code26, para_code27, para_code28, para_code29, para_code30
 FROM td_s_commpara
WHERE SUBSYS_CODE = :SUBSYS_CODE
  AND PARAM_ATTR = :PARAM_ATTR
  AND PARAM_CODE = :PARAM_CODE
  AND (EPARCHY_CODE = 'ZZZZ' OR EPARCHY_CODE = :EPARCHY_CODE)
  AND sysdate BETWEEN START_DATE AND END_DATE
ORDER BY PARAM_NAME