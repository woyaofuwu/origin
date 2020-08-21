Select COUNT(1) recordcount
  FROM tf_f_user_sale_active
 WHERE user_id=:USER_ID
 And partition_id =Mod(to_number(:USER_ID),10000)
 And ( product_id =:PRODUCT_ID OR :PRODUCT_ID='**' )
 And package_id In( Select package_Id From td_b_package a Where a.package_kind_code Like :PURCHASE_INFO And end_date>Sysdate)
 AND process_tag=:PROCESS_TAG
 AND end_date > trunc(last_day(SYSDATE))+1