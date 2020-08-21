UPDATE tf_f_postinfo
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS') - 0.00001  
 WHERE id=TO_NUMBER(:ID)
   AND id_type=:ID_TYPE
   AND post_typeset=:POST_TYPESET
   AND post_cyc=:POST_CYC
   AND post_tag='1'
   AND end_date>sysdate