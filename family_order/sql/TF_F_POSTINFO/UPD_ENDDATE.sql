UPDATE tf_f_postinfo
   SET end_date = add_months(SYSDATE,3)
 WHERE ID=:ID
   AND id_type=:ID_TYPE
   AND post_typeset LIKE '%0%'