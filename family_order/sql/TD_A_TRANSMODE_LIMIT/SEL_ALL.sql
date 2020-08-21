--IS_CACHE=Y
SELECT para_code,id,id_type,limit_tag,str1,str2,remark,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date ,eparchy_code
  FROM td_a_transmode_limit
 WHERE SYSDATE BETWEEN start_date AND end_date