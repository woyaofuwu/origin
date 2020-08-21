SELECT c.cust_name,c.pspt_type_code,c.pspt_id,p.phone
       ,p.contact,p.contact_phone,p.post_address
       ,p.work_depart
  FROM tf_f_cust_person p,tf_f_customer c
 WHERE c.cust_id=p.cust_id
   AND c.partition_id=p.partition_id
   AND c.cust_id=:CUST_ID
   AND c.partition_id=mod(:CUST_ID,10000)
   AND c.cust_type='0'