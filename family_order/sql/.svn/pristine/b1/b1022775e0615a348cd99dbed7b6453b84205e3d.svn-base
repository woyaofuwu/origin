SELECT c.serial_number,a.* 
  FROM ucr_crm1.tf_f_user_product a, 
       ucr_crm1.tf_f_cust_group   p, 
       ucr_crm1.tf_f_user         c 
 where a.product_id =:PRODUCT_ID 
   and a.end_date > sysdate 
   and a.user_id=c.user_id 
   and c.cust_id=p.cust_id 
   and p.group_id=:GROUP_ID