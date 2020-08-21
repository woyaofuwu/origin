select u.serial_number,
       c.fee_rate area_type,
       c.update_staff_id,
       c.update_time,
       c.id row_id,
       c.begin_date,
       c.end_date,
       c.remark
  from tf_f_user u, chnl_access_phone c
 where u.user_id = c.phone_nbr
   and c.state = '0'
   and c.trade_type = 2
   and u.remove_tag='0'
   and u.serial_number = :SERIAL_NUMBER