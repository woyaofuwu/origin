UPDATE tf_f_user_outnetphone
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')  
    WHERE user_id=TO_NUMBER(:USER_ID)
   AND  out_group_id=:OUT_GROUP_ID
   AND SYSDATE BETWEEN start_date AND end_date