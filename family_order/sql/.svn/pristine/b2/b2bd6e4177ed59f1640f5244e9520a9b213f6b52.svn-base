UPDATE tf_f_postinfo
   SET end_date = SYSDATE-0.00001  
 WHERE id=to_number(:ID)
   AND partition_id = mod(to_number(:ID),10000)
   AND id_type=:ID_TYPE
   AND end_date>sysdate