--IS_CACHE=Y
SELECT /*+INDEX(a PK_TD_S_RESAPPLY_PARA)*/ eparchy_code,res_type_code,para_code1,para_code2,code_type_code,para_name,rsvalue1,rsvalue2,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id 
  FROM td_s_resapply_para a
 WHERE eparchy_code=:EPARCHY_CODE
   AND res_type_code=:RES_TYPE_CODE
   AND (:PARA_CODE1 IS NULL OR para_code1=:PARA_CODE1)
   AND code_type_code=:CODE_TYPE_CODE