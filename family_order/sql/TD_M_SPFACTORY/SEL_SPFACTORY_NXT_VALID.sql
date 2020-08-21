--IS_CACHE=Y
SELECT sp_id,sp_svc_id,sp_name,sp_name_en,sp_short_name,sp_status,sp_desc,cs_tel,cs_url,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,remark,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
FROM td_m_spfactory a
WHERE sp_status = 'A'
 AND  exists(select 1 from td_m_spservice b where b.biz_type_code=:BIZ_TYPE_CODE and b.sp_id=a.sp_id 
AND (b.rsrv_str1 = :RSRV_STR1 OR :RSRV_STR1 is null))