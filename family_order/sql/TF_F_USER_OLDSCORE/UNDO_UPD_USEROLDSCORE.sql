UPDATE tf_f_user_oldscore a
   SET a.old_score_value = a.old_score_value - to_number(a.remark),
       a.remark = '业务被返校，业务类型为：'||to_char(:TRADE_TYPE_CODE)
 where user_id = to_number(:USER_ID)