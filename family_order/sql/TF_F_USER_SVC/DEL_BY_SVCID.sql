DELETE FROM tf_f_user_svc
 WHERE exists
   ( select 1
     from TD_B_PRODUCT_SVC b
     where a.service_id = b.service_id
     and b.product_id=:PRODUCT_ID)
and a.start_date <= sysdate
and a.end_date >= sysdate
and a.user_id = to_number(:USER_ID)