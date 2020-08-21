--IS_CACHE=Y
SELECT sp_id,sp_svc_id,info_code,info_value 
  FROM td_m_spservice_plus
 WHERE (sp_id=:SP_ID OR :SP_ID IS NULL)
   AND (sp_svc_id=:SP_SVC_ID OR :SP_SVC_ID IS NULL)
   AND (info_code=:INFO_CODE OR :INFO_CODE IS NULL)
   AND (info_value=:INFO_VALUE OR :INFO_VALUE IS NULL)