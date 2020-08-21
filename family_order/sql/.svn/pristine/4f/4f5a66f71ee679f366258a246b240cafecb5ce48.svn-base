select a.discnt_code,b.discnt_type_code from Tf_f_User_Discnt a ,td_b_dtype_discnt b
where a.discnt_code=b.discnt_code
and a.user_id=:USER_ID
and sysdate between a.start_date and a.end_date