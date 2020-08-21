--IS_CACHE=Y
SELECT eparchy_code,res_type_code,value_code,to_char(value_price) value_price,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id,remark 
  FROM td_m_resvalue
 WHERE eparchy_code=:EPARCHY_CODE
   AND res_type_code=:RES_TYPE_CODE
   AND value_code=:VALUE_CODE