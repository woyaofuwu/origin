--IS_CACHE=Y
SELECT eparchy_code,config_domain,config_type,config_desc,param_code,param_name,param_value1,param_value2,param_value3,param_value4,param_value5,param_value6,param_value7,param_value8,param_value9,param_value10,param_value11,param_value12,param_value13,param_value14,param_value15,param_value16,param_value17,param_value18,param_value19,param_value20,to_char(param_date1,'yyyy-mm-dd hh24:mi:ss') param_date1,to_char(param_date2,'yyyy-mm-dd hh24:mi:ss') param_date2,to_char(param_date3,'yyyy-mm-dd hh24:mi:ss') param_date3,to_char(param_date4,'yyyy-mm-dd hh24:mi:ss') param_date4,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,remark,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,update_staff_id,update_depart_id 
FROM td_b_itfcommconfig
WHERE eparchy_code=:EPARCHY_CODE
AND config_domain=:CONFIG_DOMAIN
AND config_type=:CONFIG_TYPE
AND param_code=:PARAM_CODE