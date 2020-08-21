SELECT MAX(to_number(rsrv_str7)) rsrv_str7,MAX(to_number(trade_id)) trade_id
  FROM tf_bh_trade 
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND trade_type_code=1022
   AND trunc(sysdate,'mm')=trunc(exec_time,'mm')