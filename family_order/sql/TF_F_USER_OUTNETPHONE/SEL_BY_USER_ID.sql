SELECT user_id,out_group_id,out_no,to_char(start_date,'yyyy-mm-dd') start_date,to_char(end_date,'yyyy-mm-dd') end_date 
  FROM tf_f_user_outnetphone
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND SYSDATE BETWEEN start_date AND end_date