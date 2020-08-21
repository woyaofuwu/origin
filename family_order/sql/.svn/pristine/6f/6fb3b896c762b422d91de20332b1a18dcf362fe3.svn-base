DELETE FROM td_m_res_factory
 WHERE eparchy_code=:EPARCHY_CODE
   AND ((:RES_TYPE_CODE IS NOT NULL AND res_type_code=:RES_TYPE_CODE) OR :RES_TYPE_CODE IS NULL)
   AND factory_code=:FACTORY_CODE