INSERT INTO tf_f_trans_phone(phone_code_a,phone_code_b,start_date,end_date,update_time)
 VALUES(:PHONE_CODE_A,:PHONE_CODE_B,TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS'),TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS'),TO_DATE(:UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS'))