SELECT partition_id,to_char(user_id) user_id,serial_number,score_type_code,score_type_name,to_char(score) score,to_char(update_date,'yyyy-mm-dd hh24:mi:ss') update_date,remark 
  FROM tf_f_echannel_score
 WHERE serial_number=:SERIAL_NUMBER
   AND score_type_code=:SCORE_TYPE_CODE