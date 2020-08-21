--IS_CACHE=Y
SELECT olcomserv_code,switch_type,switchcom_id,switchcom_code,remark 
  FROM td_m_olcomserv_switch
 WHERE olcomserv_code=:OLCOMSERV_CODE
   AND switch_type=:SWITCH_TYPE
   AND switchcom_id=:SWITCHCOM_ID