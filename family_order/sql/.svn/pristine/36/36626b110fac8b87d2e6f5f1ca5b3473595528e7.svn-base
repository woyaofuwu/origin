--IS_CACHE=Y
SELECT rownum ROW_NUM,para_value1 batch_id,para_code1||'-'||para_code2 para_code,para_name,to_char(update_time,'YYYY-MM-DD HH24:MI:SS') update_time
FROM td_m_res_commpara
WHERE para_attr=:PARA_ATTR AND eparchy_code=:EPARCHY_CODE
ORDER BY update_time