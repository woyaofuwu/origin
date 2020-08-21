delete From tf_f_user_foregift
  Where user_id=to_number(:USER_ID) 
 And partition_id=mod(to_number(:USER_ID),10000)
  and foregift_code<>20