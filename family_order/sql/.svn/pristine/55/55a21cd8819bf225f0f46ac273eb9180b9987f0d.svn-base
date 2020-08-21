--IS_CACHE=Y
SELECT eparchy_code,res_type_code,sale_type_code,sale_type,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id,remark,0 x_tag 
  FROM td_m_ressaletype
 WHERE eparchy_code=:EPARCHY_CODE
   AND ((:RES_TYPE_CODE IS NOT NULL AND res_type_code=:RES_TYPE_CODE) OR :RES_TYPE_CODE IS NULL)
   AND ((:SALE_TYPE_CODE IS NOT NULL AND sale_type_code=:SALE_TYPE_CODE) OR :SALE_TYPE_CODE IS NULL)