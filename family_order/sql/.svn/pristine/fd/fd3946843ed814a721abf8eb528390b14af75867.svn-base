--IS_CACHE=Y
SELECT eparchy_code,res_type_code,factory_code,factory,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id,remark,0 x_tag
  FROM td_m_res_factory
 WHERE eparchy_code=:EPARCHY_CODE
   AND ((:RES_TYPE_CODE IS NOT NULL AND res_type_code=:RES_TYPE_CODE) 
         OR :RES_TYPE_CODE IS  NULL)
   AND ((:FACTORY_CODE IS NOT NULL AND factory_code=:FACTORY_CODE) 
         OR :FACTORY_CODE IS NULL)