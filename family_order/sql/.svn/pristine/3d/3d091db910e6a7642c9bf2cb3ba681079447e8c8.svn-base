update tf_f_user_svc a
   set a.product_id = a.rsrv_str3,a.package_id = a.rsrv_str4
 where a.product_id = :PRODUCT_ID
   and a.user_id = :USER_ID
   and a.start_date < sysdate
   and a.end_date > add_months(last_day(sysdate)+1, -1)
   and a.rsrv_str3 is not null
   and a.rsrv_str4 is not null