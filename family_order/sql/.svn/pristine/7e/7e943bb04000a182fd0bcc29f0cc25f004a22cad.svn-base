select partition_id,
       user_id,
       serial_number,
       vip_id,
       vip_type_code,
       vip_class_id,
       gift_type_code,
       gift_id,
       gift_name,
       is_send_sms,
       exchange_num,
       to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date,
       to_char(update_time, 'yyyy-mm-dd hh24:mi:ss') update_time,
       update_staff_id,
       update_depart_id,
       eparchy_code,
       remark,
       rsrv_str1,
       rsrv_str2,
       rsrv_str3,
       rsrv_str4,
       rsrv_str5,
       rsrv_str6,
       rsrv_str7,
       rsrv_str8,
       rsrv_str9,
       rsrv_str10,
       to_char(rsrv_date1, 'yyyy-mm-dd hh24:mi:ss') rsrv_date1,
       to_char(rsrv_date2, 'yyyy-mm-dd hh24:mi:ss') rsrv_date2,
       to_char(rsrv_date3, 'yyyy-mm-dd hh24:mi:ss') rsrv_date3
from tf_f_vip_exchange a
WHERE 1=1
   and user_id = TO_NUMBER(:USER_ID)
   and partition_id = MOD(TO_NUMBER(:USER_ID), 10000)  
   and serial_number = :SERIAL_NUMBER
   AND update_time BETWEEN To_Date(:UPDATE_TIME, 'yyyy-mm-dd') AND To_Date(:UPDATE_TIME||' 23:59:59', 'yyyy-mm-dd hh24:mi:ss')
order by a.update_time desc