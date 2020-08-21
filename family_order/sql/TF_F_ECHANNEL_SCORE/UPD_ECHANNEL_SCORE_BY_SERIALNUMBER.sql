UPDATE tf_f_echannel_score
   SET score=(score - TO_NUMBER(:SCORE))
 WHERE serial_number=:SERIAL_NUMBER
   AND score_type_code=:SCORE_TYPE_CODE