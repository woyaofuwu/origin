--IS_CACHE=Y
SELECT discnt_code_a,discnt_code_b,
limit_tag,
to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,
to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,eparchy_code  
FROM TD_S_DISCNT_LIMIT 
WHERE (:DISCNT_CODE_A = -1 OR discnt_code_a = :DISCNT_CODE_A)
ORDER BY 1