--IS_CACHE=Y
SELECT eparchy_code,stock_pos,job,res_type_code,city_code,decode(a.add_tag,'0','员工岗位','1','代理商岗位','未知')   add_tag,res_kind_code,to_char(warnning_value_u) warnning_value_u,to_char(warnning_value_d) warnning_value_d,link_phone,manager_staff_id,rsrv_tag1,rsrv_tag2,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_num1,rsrv_num2,rsrv_num3,to_char(rsrv_num4) rsrv_num4,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id 
  FROM td_m_res_stockjob a
 WHERE eparchy_code=:EPARCHY_CODE
   AND stock_pos=:STOCK_POS
   AND res_type_code=:RES_TYPE_CODE
   AND res_kind_code=:RES_KIND_CODE
   AND add_tag=:ADD_TAG
   AND EXISTS (SELECT 1 FROM td_m_res_commpara b
                WHERE b.eparchy_code=:EPARCHY_CODE 
                  AND b.para_attr=31
                  AND b.para_code2=:RES_TYPE_CODE
                  AND b.para_code1=a.job)