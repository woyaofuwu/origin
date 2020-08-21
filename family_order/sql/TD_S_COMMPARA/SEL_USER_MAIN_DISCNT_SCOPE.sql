SELECT a.param_code,a.para_code1,a.para_code2,a.para_code3,
       a.para_code4,a.para_code5,a.para_code6,a.para_code7,
       a.para_code8,a.para_code9,a.para_code10 
       FROM td_s_commpara a,tf_f_user_discnt b
       WHERE a.param_code = b.discnt_code
       AND   a.param_attr='16'
       AND   SYSDATE BETWEEN a.start_date AND a.end_date
       AND   SYSDATE BETWEEN b.start_date AND b.end_date
       AND   b.User_Id=:USER_ID