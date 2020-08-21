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
       t.update_time
  from ucr_crm1.TF_F_FIXPHONE_PSPT_LIMT t
 where t.cust_name = :CUST_NAME
   and t.pspt_type_code in ('D', 'E', 'G', 'L', 'M')