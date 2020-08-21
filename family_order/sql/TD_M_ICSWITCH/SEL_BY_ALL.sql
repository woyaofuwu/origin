--IS_CACHE=Y
SELECT switch_id,switch_name,switch_protocal,switch_type,remark 
  FROM td_m_icswitch
 WHERE switch_id=:SWITCH_ID