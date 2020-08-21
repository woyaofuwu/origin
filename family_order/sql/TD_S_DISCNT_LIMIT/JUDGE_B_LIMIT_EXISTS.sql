--IS_CACHE=Y
SELECT discnt_code_a,discnt_code_b,limit_tag,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,eparchy_code 
  FROM td_s_discnt_limit
 WHERE discnt_code_a>0
   AND discnt_code_b=:DISCNT_CODE_B
   AND limit_tag=:LIMIT_TAG
   AND sysdate BETWEEN start_date AND end_date
   AND (eparchy_code=:EPARCHY_CODE OR eparchy_code='ZZZZ')