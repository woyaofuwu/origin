SELECT to_char(user_band_id) user_band_id,to_char(user_id) user_id,bank_card_no,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,to_char(start_time,'yyyy-mm-dd hh24:mi:ss') start_time,to_char(end_time,'yyyy-mm-dd hh24:mi:ss') end_time,reck_tag 
  FROM tf_f_bankband
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND sysdate BETWEEN start_time AND end_time