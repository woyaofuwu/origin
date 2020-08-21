SELECT a.partition_id,to_char(a.cust_id) cust_id,a.pspt_type_code,a.pspt_id,to_char(a.pspt_end_date,'yyyy-mm-dd hh24:mi:ss') pspt_end_date,a.pspt_addr,a.cust_name,a.sex,to_char(a.birthday,'yyyy-mm-dd hh24:mi:ss') birthday,a.nationality_code,a.local_native_code,a.population,a.language_code,a.folk_code,a.phone,a.post_code,a.post_address,a.fax_nbr,a.email,a.contact,a.contact_phone,a.home_address,a.work_name,a.work_depart,a.job,a.job_type_code,a.educate_degree_code,a.religion_code,a.revenue_level_code,a.marriage,a.character_type_code,a.webuser_id,a.web_passwd,a.contact_type_code,a.community_id,a.post_person,a.home_phone,a.work_phone,a.work_address 
  FROM tf_f_cust_person a, tf_f_user b
 WHERE b.user_id = :USER_ID
   AND b.partition_id = MOD(:USER_ID,10000)
   AND a.cust_id = b.cust_id
   AND a.partition_id = MOD(b.cust_id,10000)