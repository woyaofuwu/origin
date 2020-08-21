--IS_CACHE=Y
SELECT a.eparchy_code,a.stock_pos,a.job,b.para_name,a.res_type_code,a.city_code,a.add_tag,decode(a.add_tag,'0','员工岗位','1','代理商岗位','未知') para_value4,a.res_kind_code,to_char(a.warnning_value_u) warnning_value_u,to_char(a.warnning_value_d) warnning_value_d,a.link_phone,a.manager_staff_id,a.rsrv_tag1,a.rsrv_tag2,a.rsrv_str1,a.rsrv_str2,a.rsrv_str3,a.rsrv_str4,a.rsrv_num1,a.rsrv_num2,a.rsrv_num3,to_char(a.rsrv_num4) rsrv_num4,to_char(a.rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(a.rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,a.remark,to_char(a.update_time,'yyyy-mm-dd hh24:mi:ss') update_time,a.update_staff_id,a.update_depart_id,0 x_tag
FROM td_m_res_stockjob a,td_m_res_commpara b
WHERE a.job = b.para_code1
and a.add_tag = :ADD_TAG
and b.para_attr = :PARA_ATTR
and b.eparchy_code = :EPARCHY_CODE
and (a.stock_pos = :STOCK_POS or :STOCK_POS IS NULL)
and (a.job = :JOB or :JOB IS NULL )
and (a.res_type_code = :RES_TYPE_CODE or :RES_TYPE_CODE IS NULL)
and (a.res_kind_code = :RES_KIND_CODE or :RES_KIND_CODE IS NULL)
and (a.city_code = :CITY_CODE or :CITY_CODE IS NULL)