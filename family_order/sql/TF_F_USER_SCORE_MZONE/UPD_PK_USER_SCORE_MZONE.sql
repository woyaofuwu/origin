update TF_F_USER_SCORE_MZONE set score_value = :SCORE_VALUE
where user_id=TO_NUMBER(:USER_ID)
and end_date IN 
(select MIN(end_date) from TF_F_USER_SCORE_MZONE where user_id=TO_NUMBER(:USER_ID) and SYSDATE BETWEEN
start_date+0 and end_date+0)