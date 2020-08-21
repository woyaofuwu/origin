--IS_CACHE=Y
SELECT increment_serv_type,a.access_mode,a.access_tag,a.out_type,a.app_type,a.serv_id,a.serv_code,
       a.serv_name,a.serv_type,a.product_code,a.serv_area_type,a.serv_state,a.serv_kind,
       a.innovation_id,a.serv_cop_type,a.cop_serv,a.cop_product,a.product_part,a.industry,
       a.serv_intro,a.serv_price,a.serv_rate_type,a.serv_attr,a.busi_plan,a.cust_serv_plan,
       a.tech_plan,a.sms_corp_code,a.sms_serv_code,a.mms_corp_code,a.mms_serv_code,
       a.wap_corp_code,to_char(a.submit_date,'yyyy-mm-dd hh24:mi:ss') submit_date,a.serv_faq,
       a.cust_serv_docu,a.state,to_char(a.update_time,'yyyy-mm-dd hh24:mi:ss') update_time,
       a.update_staff_id,a.update_depart_id,a.remark,a.trail_state,a.trail_staff_id,
       to_char(a.trail_time,'yyyy-mm-dd hh24:mi:ss') trail_time,a.out_state,
       to_char(a.rsrv_num3) rsrv_num3,a.rsrv_str1,a.rsrv_str2,a.rsrv_str3,c.cop_serv_code rsrv_str4,
       a.rsrv_str5,c.cop_name rsrv_str6,a.rsrv_str7,a.rsrv_str8,
       to_char(a.rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,
       to_char(a.rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,
       to_char(a.rsrv_date3,'yyyy-mm-dd hh24:mi:ss') rsrv_date3,a.rsrv_tag1,a.rsrv_tag2,
       a.rsrv_tag3 
  FROM tf_cop_service a,tf_cop_service_rel b,tf_cop_partner c
 WHERE a.serv_code = :SERV_CODE
   and b.serv_id = a.serv_id
   and c.cop_id = b.cop_id
   and a.state=:STATE