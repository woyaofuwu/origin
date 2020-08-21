UPDATE tf_f_outnetgroup
   SET out_group_name=:OUT_GROUP_NAME  
 WHERE out_group_id=:OUT_GROUP_ID
   AND SYSDATE BETWEEN start_date AND end_date