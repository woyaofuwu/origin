--IS_CACHE=Y
SELECT eparchy_code,res_type_code,para_code1,para_code2,code_type_code,para_name,rsvalue1,rsvalue2,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id 
  FROM td_s_resapply_para
 WHERE eparchy_code||''=:EPARCHY_CODE
   AND (:RES_TYPE_CODE IS NULL OR res_type_code=:RES_TYPE_CODE)
   AND para_code2=:PARA_CODE2