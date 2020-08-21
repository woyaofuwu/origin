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