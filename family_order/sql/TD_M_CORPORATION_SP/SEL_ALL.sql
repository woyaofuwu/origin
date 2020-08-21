--IS_CACHE=Y
SELECT sp_id,sp_code,sp_name,sp_type,sp_attr,serv_code,in_province,con_province,plat_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,sp_desc,cs_tel,cs_url,contact,sp_status,sp_short_name,sp_name_en,opr_source,to_char(first_date,'yyyy-mm-dd hh24:mi:ss') first_date,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,remark,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,file_name 
  FROM td_m_corporation_sp
 WHERE sysdate BETWEEN start_date AND end_date