select trade_id from TF_B_TRADE_DISCNT t ,Tf_f_User u
  where 1=1
   and t.user_id = u.user_id
   and t.accept_month= TO_NUMBER(to_char(sysdate,'mm')) 
   and u.serial_number = :SERIAL_NUMBER 
   and t.Discnt_Code in ( select TO_NUMBER(c.para_code4) from td_s_commpara c where  c.param_attr=4502 and  c.param_name like '%飞信%') 
   and t.start_date>=   trunc(sysdate,'month')