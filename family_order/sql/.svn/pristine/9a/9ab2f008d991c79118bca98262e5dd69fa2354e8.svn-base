UPDATE tf_f_user_oldscore
   SET REMARK = '积分捐赠：'||to_char(:SCORE_VALUE)||'分',
       OLD_SCORE_VALUE = 0  
 WHERE user_id = (select user_id from tf_f_user where remove_tag='0' and serial_number=to_char(:SERIAL_NUMBER))