SELECT to_char(a.cust_id) cust_id,c.cust_name
       ,a.serial_number,a.product_id
       ,a.brand_code,a.remove_tag
       ,c.pspt_type_code,c.pspt_id,a.score_value
       ,a.credit_value,a.city_code,p.phone
       ,p.contact,p.contact_phone,p.post_address
       ,p.work_depart,a.user_id,a.user_state_codeset
  FROM 
     (SELECT u.cust_id,u.serial_number,u.product_id
            ,u.brand_code,u.remove_tag,u.score_value
            ,u.credit_value,u.city_code
            ,u.user_id,u.user_state_codeset 
      FROM tf_f_user u 
     WHERE u.serial_number=:SERIAL_NUMBER
       AND u.remove_tag=:REMOVE_TAG
       AND u.user_diff_code='0'
       ) a,tf_f_cust_person p,tf_f_customer c
 WHERE a.cust_id=p.cust_id
   AND a.cust_id=c.cust_id
   AND c.cust_type='0'