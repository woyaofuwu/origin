select b.partition_id,to_char(b.ec_user_id) ec_user_id,to_char(b.user_id) user_id,to_char(b.serial_number) serial_number,b.ec_serial_number,
  b.biz_code,a.biz_type_code,b.biz_name,a.biz_status,a.biz_attr,b.start_date,b.end_date,a.serv_code,
  a.group_id,a.remark
  from tf_f_user_grp_platsvc a, tf_f_user_grp_meb_platsvc b
 where a.serial_number = b.ec_serial_number
   and a.biz_code = b.biz_code
   and a.partition_id = MOD(b.ec_user_id,10000)
   and a.user_id = b.ec_user_id
   and a.serv_code = b.serv_code
   and b.serial_number =:SERIAL_NUMBER
   and a.serv_code=:SERV_CODE
   and b.ec_user_id = to_number(:EC_USER_ID)
   and a.end_date>sysdate
   and b.end_date>sysdate