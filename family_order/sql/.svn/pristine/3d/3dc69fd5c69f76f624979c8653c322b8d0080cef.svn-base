UPDATE tf_f_outnetgroup
   SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')  
 WHERE out_group_id=:OUT_GROUP_ID
   AND SYSDATE BETWEEN start_date AND end_date