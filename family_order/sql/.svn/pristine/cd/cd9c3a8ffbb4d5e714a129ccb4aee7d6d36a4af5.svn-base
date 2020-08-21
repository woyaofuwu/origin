select distinct u.serial_number as RES_COUNT,u.serial_number,u.user_id   
from tf_f_user partition(PAR_TF_F_USER_1) u, tf_f_customer c  Where u.cust_id = c.cust_id     
 and c.partition_id = mod(u.cust_id, 10000)    
 and u.city_code not in ('HNSJ', 'HNHN')    
 and c.cust_name = :CUST_NAME    
 and c.pspt_id = :PSPT_ID    
 and c.is_real_name = '1'    
 and to_char(u.open_date,'yyyymmdd') >= to_char(sysdate-7,'yyyymmdd')    
 and exists  (SELECT 1          
  FROM tf_F_user_product partition(PAR_TF_F_USER_PRODUCT_1) p          where p.user_id = u.user_id           
   and p.partition_id = mod(u.user_id, 10000)           
   and p.main_tag = '1'            
   and p.brand_code in ('TDYD') )
