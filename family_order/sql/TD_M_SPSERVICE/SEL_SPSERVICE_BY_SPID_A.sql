--IS_CACHE=Y
SELECT a.sp_id,b.sp_name,b.sp_name_en,a.sp_svc_id,a.biz_type,a.biz_desc,a.biz_type_code,a.access_model,trim(to_char(a.price/1000,'99990.00')) price,decode(a.billing_type,'0','免费','1','按条计费','2','包月计费','3','包时计费','4','包次计费','未知') billing_type,a.biz_status,a.prov_addr,a.prov_port,a.usage_desc,a.intro_url,a.foregift_code,to_char(a.foregift) foregift,a.rsrv_str1,a.rsrv_str2,a.rsrv_str3,a.rsrv_str4,a.rsrv_str5,a.rsrv_str9,a.remark,a.update_staff_id,a.update_depart_id,to_char(a.update_time,'yyyy-mm-dd hh24:mi:ss') update_time,b.cs_tel
FROM td_m_spservice a, td_m_spfactory b
WHERE a.sp_id = :SP_ID
AND b.sp_id = a.sp_id
AND a.biz_status NOT IN ('N','E')
AND EXISTS(SELECT 1
FROM td_s_tag
WHERE tag_code='PUB_CUR_PROVINCE'
AND use_tag='0'
AND start_date+0<sysdate
AND subsys_code='PUB'
AND end_date+0>=sysdate
AND INSTR(DECODE(tag_info,'SHXI','1',NVL(a.rsrv_str4,'0')),NVL(a.rsrv_str4,'0'))>0
)