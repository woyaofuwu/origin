--IS_CACHE=Y
SELECT eparchy_code,para_code1,para_code2,code_type_code,para_name,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,action_code,rsvalue1,rsvalue2,rsrv_tag1,rsrv_tag2,rsrv_tag3,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id 
  FROM td_m_goods_para
 WHERE eparchy_code=:EPARCHY_CODE
   AND para_code1=:PARA_CODE1
   AND para_code2=:PARA_CODE2