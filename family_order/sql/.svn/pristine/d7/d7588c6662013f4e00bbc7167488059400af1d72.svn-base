UPDATE tf_f_user_product
  SET end_date = SYSDATE,update_time=SYSDATE,update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID
  WHERE user_id = to_number(:USER_ID)
    AND partition_id = MOD(to_number(:USER_ID),10000)
    AND end_date > SYSDATE
    AND end_date > start_Date