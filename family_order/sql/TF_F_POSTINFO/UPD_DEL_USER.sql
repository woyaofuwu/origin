UPDATE tf_f_postinfo
   SET end_date = SYSDATE-0.00001  
 WHERE id=:ID
   AND id_type=:ID_TYPE
   AND post_cyc=:POST_CYC
   AND end_date>sysdate