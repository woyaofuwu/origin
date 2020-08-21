--IS_CACHE=Y
SELECT data_code,data_name,DECODE(data_type,'0','资源权限','数据特权') data_type,class_code,right_type,to_char(help_index) help_index,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id 
  FROM td_m_dataright
 WHERE (:DATA_CODE IS NULL OR data_code=:DATA_CODE)
   AND (:DATA_TYPE IS NULL OR data_type=:DATA_TYPE)
   AND (:CLASS_CODE IS NULL OR class_code=:CLASS_CODE)
 ORDER BY class_code,data_code