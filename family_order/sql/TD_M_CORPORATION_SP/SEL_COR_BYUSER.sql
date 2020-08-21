SELECT DISTINCT sp_id,a.sp_code sp_code,sp_name,sp_type,sp_attr,serv_code,in_province,con_province,plat_code,to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,sp_desc,cs_tel,cs_url,contact,sp_status,sp_short_name,sp_name_en,a.opr_source,to_char(a.first_date,'yyyy-mm-dd hh24:mi:ss') first_date,a.rsrv_str1,a.rsrv_str2,a.rsrv_str3,a.rsrv_str4,a.rsrv_str5,a.remark,a.update_staff_id,a.update_depart_id,to_char(a.update_time,'yyyy-mm-dd hh24:mi:ss') update_time,file_name 
  FROM td_m_corporation_sp a, tf_f_user_plat_order b
 WHERE b.partition_id = MOD(:USER_ID, 10000)
   AND b.User_Id = :USER_ID
   AND b.sp_code = a.sp_code
   AND SYSDATE BETWEEN a.Start_Date AND a.End_Date
   AND SYSDATE BETWEEN b.start_date AND b.end_date