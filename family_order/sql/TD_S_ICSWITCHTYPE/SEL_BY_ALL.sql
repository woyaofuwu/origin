--IS_CACHE=Y
SELECT switch_type,switch_type_name,remark 
  FROM td_s_icswitchtype
 WHERE switch_type=:SWITCH_TYPE