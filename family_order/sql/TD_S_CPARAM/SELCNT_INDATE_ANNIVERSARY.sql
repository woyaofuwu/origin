Select to_number(to_char(Sysdate,'yy'))-to_number(to_char(in_date,'yy')) recordcount
From tf_f_user Where user_id=:USER_ID
And to_char(Sysdate,'mmdd')=to_char(in_date,'mmdd')