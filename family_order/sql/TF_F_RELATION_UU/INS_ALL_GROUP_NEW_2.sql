INSERT INTO tf_f_relation_uu(partition_id,user_id_a,serial_number_a,user_id_b,serial_number_b,relation_type_code,
role_code_a,role_code_b,orderno,short_code,start_date,end_date,update_time)
SELECT a.partition_id,:NEW_USER_ID,:NEW_SERIAL_NUMBER,a.User_Id_b,a.serial_number_b,a.relation_type_code,
a.role_code_a,:ROLE_CODE,a.Orderno,a.short_code,SYSDATE,a.End_Date,sysdate
  FROM tf_f_relation_uu a
 WHERE a.user_id_a = :OLD_USER_ID
   AND a.serial_number_a = :OLD_SERIAL_NUMBER
   AND end_date > SYSDATE