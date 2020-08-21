--IS_CACHE=Y
SELECT sp_id,sp_svc_id,biz_type,biz_desc,biz_type_code,access_model,to_char(price) price,billing_type,biz_status,prov_addr,prov_port,usage_desc,intro_url,foregift_code,to_char(foregift) foregift,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10,rsrv_str11,rsrv_str12,rsrv_str13,rsrv_str14,remark,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM td_m_spservice a
 WHERE a.rsrv_str7 = :RSRV_STR7
   AND a.biz_type_code =:BIZ_TYPE_CODE
   AND a.biz_status NOT IN ('N','E')
   AND EXISTS(
            SELECT 1 FROM td_s_commpara b 
             WHERE b.subsys_code='CSM'
               AND b.param_attr=3038
               AND sysdate BETWEEN b.start_date AND b.end_date
               AND b.param_code =:BIZ_TYPE_CODE
               AND b.para_code2 like :RSRV_STR8||'%'
               AND b.para_code3 =a.sp_svc_id
               AND b.para_code4 =:RSRV_STR7)