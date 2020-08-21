SELECT uu.partition_id,to_char(uu.user_id_a) user_id_a,uu.serial_number_a,to_char(uu.user_id_b) user_id_b,uu.serial_number_b,uu.relation_type_code,uu.role_code_a,uu.role_code_b,uu.orderno,uu.short_code,to_char(uu.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(uu.end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_f_relation_uu uu, tf_f_user usr
 WHERE uu.user_id_a= usr.user_id
   AND usr.cust_id=TO_NUMBER(:CUST_ID)