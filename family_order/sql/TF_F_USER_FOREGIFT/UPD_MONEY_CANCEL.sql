UPDATE tf_f_user_foregift
   SET money=money - TO_NUMBER(:MONEY)  
 WHERE user_id=TO_NUMBER(:USER_ID)
   AND foregift_code=:FOREGIFT_CODE