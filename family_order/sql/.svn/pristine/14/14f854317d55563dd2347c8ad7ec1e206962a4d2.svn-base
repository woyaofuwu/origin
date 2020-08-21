SELECT COUNT(1) recordcount
FROM td_s_commpara a
WHERE subsys_code = 'CSM'
AND param_attr = 1032
AND SYSDATE BETWEEN start_date AND end_date
AND (eparchy_code = :EPARCHY_CODE OR eparchy_code = 'ZZZZ')
AND param_code = :TRADE_TYPE_CODE
AND EXISTS (
SELECT 1 FROM tf_f_relation_uu b WHERE b.user_id_b=:USER_ID
AND b.relation_type_code=a.para_code1
AND (b.role_code_b=a.para_code2 OR a.para_code2 IS NULL)
AND b.end_date>trunc(last_day(SYSDATE))+1)