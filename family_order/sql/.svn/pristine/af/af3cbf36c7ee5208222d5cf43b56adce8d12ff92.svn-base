--IS_CACHE=Y
SELECT sp_id,sp_svc_id,sp_name,sp_name_en,sp_short_name,sp_status,sp_desc,cs_tel,cs_url,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,remark,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
FROM td_m_spfactory
WHERE sp_status NOT IN ('N')
AND EXISTS(SELECT 1
FROM td_s_tag
WHERE tag_code='PUB_CUR_PROVINCE'
AND use_tag='0'
AND start_date+0<sysdate
AND subsys_code='PUB'
AND end_date+0>=sysdate
AND INSTR(DECODE(tag_info,'SHXI','1',NVL(rsrv_str4,'0')),NVL(rsrv_str4,'0'))>0
)