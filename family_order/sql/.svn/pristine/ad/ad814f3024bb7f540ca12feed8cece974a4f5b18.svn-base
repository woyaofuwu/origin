UPDATE tf_f_user_oldscore
   SET  old_score_value = old_score_value - to_number(:SCORE_VALUE),
        remark = '积分捐赠：'||to_char(:SCORE_VALUE)||'分'
 WHERE user_id = (select user_id from tf_f_user where remove_tag='0' and serial_number=to_char(:SERIAL_NUMBER))