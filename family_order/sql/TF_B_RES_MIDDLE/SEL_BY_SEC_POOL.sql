select SUBSTR(res_no,1,3) res_no,to_char(COUNT(1)) RSRV_NUM1
from   tf_b_res_middle a 
where  log_id=:LOG_ID
and    res_type_code='0'
and    RES_STATE_CODE NOT IN('2','3','4','5')
and    rsrv_str1='3'      --表示群池
and    rsrv_str2=:RSRV_STR2
AND    substr(res_no,1,3) IN (SELECT code_area_code FROM td_m_codearea)
group  by SUBSTR(res_no,1,3)