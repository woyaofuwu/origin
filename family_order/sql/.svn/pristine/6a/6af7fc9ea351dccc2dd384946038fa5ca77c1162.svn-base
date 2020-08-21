SELECT to_char(charge_id) charge_id,trade_type_code,to_char(trans_money) trans_money,serial_number_a,serial_number_b,to_char(user_id_a) user_id_a,to_char(user_id_b) user_id_b,role_a,role_b,to_char(trade_fee) trade_fee,trade_eparchy_code,trade_city_code,trade_depart_id,trade_staff_id,to_char(exec_time,'yyyy-mm-dd hh24:mi:ss') exec_time,cancel_tag,prevaluec1,prevaluec2,prevaluec3,to_char(prevaluen1) prevaluen1,to_char(prevaluen2) prevaluen2,to_char(prevaluen3) prevaluen3 
  FROM tf_a_scptranlog
 WHERE user_id_a=TO_NUMBER(:USER_ID_A)
   AND exec_time>TO_DATE(:STARTDATE, 'YYYY-MM-DD HH24:MI:SS')
   AND exec_time>TO_DATE(:ENDDATE, 'YYYY-MM-DD HH24:MI:SS')
   AND cancel_tag='0'