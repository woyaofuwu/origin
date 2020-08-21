SELECT to_char(trade_id) trade_id,accept_month,to_char(id) id,id_type,class_name,class_level,max_users,modify_tag,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,rsrv_str1,rsrv_str2,to_char(rsrv_num3) rsrv_num3,to_char(rsrv_num4) rsrv_num4,to_char(rsrv_dat5,'yyyy-mm-dd hh24:mi:ss') rsrv_dat5,to_char(rsrv_dat6,'yyyy-mm-dd hh24:mi:ss') rsrv_dat6 
  FROM tf_b_trade_class
 WHERE trade_id=:TRADE_ID
and accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))