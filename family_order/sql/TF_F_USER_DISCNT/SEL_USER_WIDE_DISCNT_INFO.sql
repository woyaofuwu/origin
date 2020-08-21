select t.* from Tf_f_User_Discnt t 
where  t.user_id=:KD_USER_ID
and sysdate < t.end_date