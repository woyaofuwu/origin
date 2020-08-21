SELECT COUNT(1) recordcount
  FROM  tf_f_specific_discnt a
 WHERE id=:USER_ID
   AND id_type_code='0'
   AND disn_type_code=:DISN_TYPE_CODE
   AND end_acyc_id >=(SELECT acyc_id FROM td_a_acycpara WHERE SYSDATE BETWEEN acyc_start_time AND acyc_end_time-1/24/3600)
   AND start_acyc_id<=end_acyc_id