SELECT channel_no 
  FROM tg_user_id_channel_no
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND remove_tag='0'
   AND eparchy_code=:EPARCHY_CODE