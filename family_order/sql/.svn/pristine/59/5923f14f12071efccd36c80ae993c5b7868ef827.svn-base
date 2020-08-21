UPDATE tf_f_trans_phone
   SET phone_code_a=:PHONE_CODE_A,phone_code_b=:PHONE_CODE_B,start_date=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS'),end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'),update_time=SYSDATE
 WHERE phone_code_a=:PHONE_CODE_A
   AND end_date>SYSDATE