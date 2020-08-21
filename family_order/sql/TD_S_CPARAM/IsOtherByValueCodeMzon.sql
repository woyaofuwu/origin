Select /*+ first_rows(1)*/COUNT(1) recordcount
FROM tf_f_user_sale_active a
 Where  product_id=:RSRV_VALUE
   AND (RSRV_STR4 =:RSRV_STR4 )
   AND end_date > SYSDATE
   AND end_date > start_date
   and rownum < 2