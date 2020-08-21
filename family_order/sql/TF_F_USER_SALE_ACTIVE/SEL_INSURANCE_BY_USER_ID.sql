select a.user_id,a.package_id,a.campn_id,
       a.campn_code,a.campn_name,a.package_name,
    to_char(a.accept_date,'yyyy-mm-dd hh24:mi:ss') accept_date,
    to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,
    to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,
    b.rsrv_str8,b.res_code 
From tf_f_user_sale_active a ,tf_f_user_sale_goods b
Where a.user_id = b.user_id
And a.product_id =b.product_id
And a.package_id =b.package_id
And a.user_id =:USER_ID
And a.partition_id =Mod(:USER_ID,10000)
And b.user_id =:USER_ID
And b.partition_id =Mod(:USER_ID,10000)
And b.res_type_code='BX' 
AND a.end_date　> sysdate