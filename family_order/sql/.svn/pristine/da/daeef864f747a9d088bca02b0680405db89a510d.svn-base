--IS_CACHE=Y
SELECT a.sp_id,b.sp_name,b.sp_name_en,a.sp_svc_id,a.biz_type,a.biz_desc,a.biz_type_code,a.access_model,to_char(a.price) price,a.billing_type,a.biz_status,a.prov_addr,a.prov_port,a.usage_desc,a.intro_url,a.foregift_code,to_char(a.foregift) foregift,a.rsrv_str1,a.rsrv_str2,a.rsrv_str3,a.rsrv_str4,a.rsrv_str5,a.rsrv_str9,a.remark,a.update_staff_id,a.update_depart_id,to_char(a.update_time,'yyyy-mm-dd hh24:mi:ss') update_time,b.cs_tel
  FROM td_m_spservice a, td_m_spfactory b
 WHERE a.sp_svc_id = :SP_SVC_ID
   AND a.biz_status NOT IN ('N','E')
   AND b.sp_id = a.sp_id
   AND a.rsrv_str4='1'