SELECT to_char(trade_id) trade_id,accept_month,to_char(user_id) user_id,service_id,serv_para1,serv_para2,serv_para3,serv_para4,serv_para5,serv_para6,serv_para7,serv_para8,modify_tag,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_b_trade_svc
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month=:ACCEPT_MONTH
   AND modify_tag!='2'
