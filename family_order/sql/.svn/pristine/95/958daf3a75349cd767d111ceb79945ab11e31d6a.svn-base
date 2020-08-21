UPDATE tf_f_cust_person
   SET(pspt_type_code,pspt_id,pspt_end_date,pspt_addr,cust_name,sex,birthday,nationality_code,local_native_code,population,language_code,folk_code,phone,post_code,post_address,fax_nbr,email,contact,contact_phone,home_address,work_name,work_depart,job,job_type_code,educate_degree_code,religion_code,revenue_level_code,marriage,character_type_code,webuser_id,web_passwd,contact_type_code,community_id )
 = (SELECT pspt_type_code,pspt_id,pspt_end_date,pspt_addr,cust_name,sex,birthday,nationality_code,local_native_code,population,language_code,folk_code,phone,post_code,post_address,fax_nbr,email,contact,contact_phone,home_address,work_name,work_depart,job,job_type_code,educate_degree_code,religion_code,revenue_level_code,marriage,character_type_code,webuser_id,web_passwd,contact_type_code,community_id
      FROM TF_B_TRADE_PERSON_BAK
     WHERE trade_id = TO_NUMBER(:TRADE_ID)
       AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
       AND cust_id = TO_NUMBER(:CUST_ID))
 WHERE cust_id = TO_NUMBER(:CUST_ID)
   AND partition_id = MOD(TO_NUMBER(:CUST_ID),10000)