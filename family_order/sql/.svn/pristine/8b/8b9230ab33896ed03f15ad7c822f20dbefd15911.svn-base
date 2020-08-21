select o.trade_id,
       to_char(o.user_id) user_id,
       u.serial_number,
       decode(o.rsrv_str1, 0, '负责人', 1, '店员', 2, '无卡店员') cust_type,
       o.rsrv_str2 chnl_type,
       o.rsrv_str3 chnl_code,
       o.rsrv_str4 chnl_level,
       o.rsrv_str5 staff_code,
       c.cumu_name,
       decode(o.rsrv_str6 || o.rsrv_str7,
              '1401',
              '话音补贴',
              '1402',
              '话音补贴',
              '1403',
              '新业务补贴') sub_name,
       o.rsrv_str8 city_code,
       o.rsrv_str9 chnl_name,
       to_char(o.start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(o.end_date, 'yyyy-mm-dd hh24:mi:ss') end_date,
       o.staff_id,
       o.depart_id
  from tf_f_user_other o, tf_f_user u, (select * from chnl_cu_cumuinfo t where t.update_state='1') c
   where o.user_id = u.user_id
   and o.rsrv_value_code = 'CHNL'
   and u.remove_tag = '0'
   and o.end_date > sysdate
   and (o.rsrv_str8 = :CITY_CODE OR :CITY_CODE is null)
   and (o.rsrv_str3 = :CHNL_CODE OR :CHNL_CODE is null)
   and (o.rsrv_str9 like '%' || :CHNL_NAME || '%' OR :CHNL_NAME is null)
   and (u.serial_number = :SERIAL_NUMBER OR :SERIAL_NUMBER is null)
   and  o.rsrv_str5 = c.cumu_id(+)
 order by o.user_id, o.start_date
 