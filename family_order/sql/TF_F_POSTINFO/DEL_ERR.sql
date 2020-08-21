DELETE FROM tf_f_postinfo
 WHERE id=TO_NUMBER(:ID)
   AND id_type=:ID_TYPE
   AND start_date > end_date