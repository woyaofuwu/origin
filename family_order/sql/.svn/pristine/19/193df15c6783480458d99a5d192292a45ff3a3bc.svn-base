SELECT to_char(a.cust_id) cust_id,a.cust_name
       ,u.serial_number,u.product_id
       ,u.brand_code,u.remove_tag
       ,a.pspt_type_code,a.pspt_id,u.score_value
       ,u.credit_value,u.city_code,a.phone
       ,a.contact,a.contact_phone,a.post_address
       ,a.work_depart,u.user_id,u.user_state_codeset
  FROM (
        SELECT c.partition_id,c.cust_id,c.cust_name,
               c.pspt_type_code,c.pspt_id,p.phone,p.contact
               ,p.contact_phone,p.post_address,p.work_depart
          FROM tf_f_cust_person p,tf_f_customer c
         WHERE c.cust_id=p.cust_id
           AND c.partition_id=p.partition_id
           AND c.cust_type='0'
           AND c.pspt_id=:PSPT_ID
           AND p.pspt_id=:PSPT_ID
           AND p.pspt_type_code=:PSPT_TYPE_CODE
           AND c.pspt_type_code=:PSPT_TYPE_CODE
           AND p.cust_name=:CUST_NAME
           AND c.cust_name=:CUST_NAME           
        ) a,tf_f_user u 
 WHERE u.cust_id=a.cust_id 
   AND u.remove_tag=:REMOVE_TAG
   AND u.user_diff_code='0'