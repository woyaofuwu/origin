SELECT partition_id,
       user_id,
       sp_id,
       biz_type_code,
       org_domain,
       opr_source,
       sp_svc_id,
       start_date,
       end_date,
       biz_state_code,
       billflg,
       rsrv_num1,
       rsrv_num2,
       rsrv_num3,
       rsrv_num4,
       rsrv_num5,
       rsrv_str1,
       rsrv_str2,
       rsrv_str3,
       rsrv_str4,
       rsrv_str5,
       rsrv_date1,
       rsrv_date2,
       rsrv_date3,
       access_model,
       remark,
       update_staff_id,
       update_depart_id,
       update_time,
       biz_type,
       sp_short_name
FROM 
(SELECT a.partition_id,
       to_char(a.user_id) user_id,
       c.sp_id,
       a.biz_type_code,
       a.org_domain,
       a.opr_source,
       a.sp_svc_id,
       TO_CHAR(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,
       a.biz_state_code,
       b.billing_type billflg,
       a.rsrv_num1,
       a.rsrv_num2,
       a.rsrv_num3,
       b.rsrv_str8 rsrv_num4,
       to_char(a.rsrv_num5) rsrv_num5,
       b.biz_desc rsrv_str1,
       b.rsrv_str2,
       b.rsrv_str3,
       b.rsrv_str4,
       TRIM(to_char(b.price/1000,'999990.00')) rsrv_str5,
       b.rsrv_str6 rsrv_date1,
       b.rsrv_str7 rsrv_date2,
       c.sp_name rsrv_date3,
       TO_CHAR(a.rsrv_date3,'yyyy-mm-dd hh24:mi:ss') access_model,
       a.remark,
       a.update_staff_id,
       a.update_depart_id,
       to_char(a.rsrv_date1,'yyyy-mm-dd hh24:mi:ss') update_time,
       b.biz_type,
       c.sp_short_name  sp_short_name,
       b.rsrv_str10,
       d.para_code15
FROM tf_f_user_mbmp_sub a, td_m_spservice b,td_m_spfactory c,td_s_commpara d
WHERE a.user_id = TO_NUMBER(:USER_ID)
   AND a.partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND a.end_date > SYSDATE AND a.biz_state_code <> 'E' AND a.biz_state_code <> 'P' 
   AND a.sp_id = b.sp_id(+)
  AND a.sp_svc_id = trim(b.sp_svc_id(+))
  AND a.sp_id=c.sp_id
  AND d.param_attr='3'
  AND d.param_code=a.biz_type_code
  AND d.para_code1=a.org_domain
  and a.biz_type_code not in ('03','04','05','13')
UNION ALL
SELECT a.partition_id,
       to_char(a.user_id) user_id,
       c.sp_id,
       a.biz_type_code,
       a.org_domain,
       a.opr_source,
       b.sp_svc_id,
       TO_CHAR(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,
       a.biz_state_code,
       b.billing_type billflg,
       a.rsrv_num1,
       a.rsrv_num2,
       a.rsrv_num3,
       b.rsrv_str8 rsrv_num4,
       to_char(a.rsrv_num5) rsrv_num5,
       b.biz_desc rsrv_str1,
       b.rsrv_str2,
       b.rsrv_str3,
       b.rsrv_str4,
       TRIM(to_char(b.price/1000,'999990.00')) rsrv_str5,
       b.rsrv_str6 rsrv_date1,
       b.rsrv_str7 rsrv_date2,
       c.sp_name rsrv_date3,
       TO_CHAR(a.rsrv_date3,'yyyy-mm-dd hh24:mi:ss') access_model,
       a.remark,
       a.update_staff_id,
       a.update_depart_id,
       to_char(a.rsrv_date1,'yyyy-mm-dd hh24:mi:ss') update_time,
       b.biz_type,
       c.sp_short_name  sp_short_name,
       b.rsrv_str10,
       d.para_code15
FROM tf_f_user_mbmp a, td_m_spservice b,td_m_spfactory c,td_s_commpara d,td_s_commpara e
WHERE a.user_id = TO_NUMBER(:USER_ID)
   AND a.partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND a.end_date > SYSDATE AND a.biz_state_code <> 'E' AND a.biz_state_code <> 'P' 
  AND d.param_attr='3'
  AND d.param_code=a.biz_type_code
  AND d.para_code1=a.org_domain
  AND e.param_attr='2568'
  AND e.param_code=a.biz_type_code
  AND e.para_code1 = b.sp_id(+)
  AND e.para_code2 = trim(b.sp_svc_id(+))
  AND e.para_code1=c.sp_id
  and a.biz_type_code not in ('03','04','05','13'))
ORDER BY rsrv_str10 DESC,para_code15,biz_type_code,sp_id