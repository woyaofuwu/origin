--IS_CACHE=Y
SELECT eparchy_code,res_type_code,res_trade_type_code,dao_table_name,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id,0 x_tag
  FROM td_s_restable
 WHERE eparchy_code=:EPARCHY_CODE
   AND res_type_code=:RES_TYPE_CODE