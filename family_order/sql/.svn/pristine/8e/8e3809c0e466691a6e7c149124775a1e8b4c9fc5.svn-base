SELECT decode(RSRV_STR4,'0','普通有价值客户','1','特殊身份客户','2','被担保客户') RSRV_STR4,rsrv_str3,(SELECT serial_number FROM tf_f_user WHERE user_id =  to_number(:USER_ID)  and partition_id = mod(to_number(:USER_ID),10000) AND ROWNUM<2)  SERIAL_NUMBER
 FROM 
     (SELECT * FROM  Tf_f_User_Other WHERE user_id = to_number(:USER_ID)  AND rsrv_value_code ='CRED' ORDER BY end_date DESC )
WHERE  ROWNUM<2