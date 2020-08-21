SELECT depart_id,depart_log_time,aspay_fee,to_char(COUNT(*)) rsrv_str1
FROM tf_a_departreturnlog  
WHERE depart_id=:DEPART_ID AND  return_reason_code <> '0' AND return_reason_code <> '1' 
GROUP BY depart_id,depart_log_time,aspay_fee
MINUS
SELECT depart_id,depart_log_time,aspay_fee,to_char(COUNT(*)) rsrv_str1
FROM tf_a_departreturnlog  
WHERE depart_id=:DEPART_ID AND  return_reason_code ='1'
GROUP BY depart_id,depart_log_time,aspay_fee
ORDER BY depart_id,depart_log_time