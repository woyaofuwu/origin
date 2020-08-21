SELECT a.product_id,a.org_domain,a.cop_id,a.biz_code,a.discnt_code,a.discnt_name,a.discnt_mode,a.force_tag,a.rela_discnt_code,to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,a.update_staff_id,a.update_depart_id,to_char(a.update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM td_b_siservice_discnt a,tf_f_user_discnt b
 WHERE b.partition_id = MOD(to_number(:USER_ID),10000)
   AND b.user_id = :USER_ID
   AND a.biz_code=:BIZ_CODE
   AND a.rela_discnt_code= b.discnt_code
   AND SYSDATE BETWEEN a.start_date AND a.end_date