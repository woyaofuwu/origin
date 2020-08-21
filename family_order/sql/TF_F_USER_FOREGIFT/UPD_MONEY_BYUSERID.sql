UPDATE tf_f_user_foregift
   SET money=0 
 where user_id=to_number(:USER_ID)
  and foregift_code<>20