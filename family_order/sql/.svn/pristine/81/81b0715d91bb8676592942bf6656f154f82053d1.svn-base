select t.pspt_type_code,
       t.pspt_id,
       t.cust_name,
       t.limit_count,
       t.limit_tag,
       t.start_date,
       t.end_date,
       t.eparchy_code,
       t.update_staff_id,
       t.update_depart_id,
       t.update_time,
       t.rsrv_str1,
       t.rsrv_str2,
       t.rsrv_str3,
       t.remark,
       t.rsrv_str4,
       t.rsrv_str5,
       t.rsrv_str6,
       t.rsrv_str7,
       t.rsrv_str8,
       t.rsrv_str9,
       t.rsrv_str10
  from ucr_crm1.TF_F_CUST_PSPT_LIMIT t
 where t.pspt_id = :PSPT_ID
   and t.rsrv_str1 = :RSRV_STR1
   and t.pspt_type_code in ('D', 'E', 'G', 'L', 'M')