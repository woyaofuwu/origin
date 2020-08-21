--IS_CACHE=Y
SELECT a.eparchy_code,a.para_attr, a.para_code1, a.para_code2, a.para_name, a.para_value1, a.para_value2, a.para_value3, a.para_value4, a.para_value5, a.para_value6, a.para_value7, a.para_value8, b.para_value1 para_value9, b.para_value2 para_value10, b.para_value3 para_value11, b.para_value4 para_value12,b.para_code2 remark, b.rdvalue1, b.rdvalue2, b.update_time, b.update_staff_id, b.update_depart_id
FROM td_m_res_commpara a,td_m_res_commpara b  
WHERE a.para_attr = 33 and b.para_attr = 34 and a.para_code1 = b.para_code1
AND sysdate between b.rdvalue1 and b.rdvalue2
AND a.eparchy_code = :EPARCHY_CODE
AND (:PARA_CODE1 IS NULL OR a.para_code1 = :PARA_CODE1)
AND (:PARA_CODE2 IS NULL OR a.para_code2 = :PARA_CODE2)