SELECT partition_id,to_char(cust_id) cust_id,pspt_type_code,pspt_id,to_char(pspt_end_date,'yyyy-mm-dd hh24:mi:ss') pspt_end_date,
       pspt_addr,cust_name,sex,to_char(birthday,'yyyy-mm-dd hh24:mi:ss') birthday,nationality_code,local_native_code,population,
       language_code,folk_code,phone,post_code,post_address,fax_nbr,email,contact,contact_phone,home_address,work_name,work_depart,
       job,job_type_code,educate_degree_code,religion_code,revenue_level_code,marriage,character_type_code,webuser_id,web_passwd,contact_type_code,community_id 
  FROM tf_f_cust_person
 WHERE cust_name LIKE '%'||:CUST_NAME||'%'