SELECT to_char(user_id) user_id,acyc_id,detail_item_code,to_char(used) used,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time 
  FROM ts_a_freesource
 WHERE user_id=TO_NUMBER(:USER_ID)