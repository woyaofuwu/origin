update tf_f_user_sale_active T
set t.rsrv_tag2='Y'
 where t.product_id =:PRODUCT_ID
   and t.package_id =:PACKAGE_ID
   and t.USER_ID = :USER_ID
   and t.process_tag = '0' 
   and sysdate < t.end_date
   and t.start_date < t.end_date