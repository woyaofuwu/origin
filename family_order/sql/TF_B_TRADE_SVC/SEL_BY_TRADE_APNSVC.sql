SELECT to_char(trade_id) trade_id,accept_month,to_char(user_id) user_id,service_id,serv_para1,serv_para2,serv_para3,serv_para4,serv_para5,serv_para6,serv_para7,serv_para8,modify_tag,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_b_trade_svc
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND service_id in (
  select service_id 
		  FROM td_o_service_olcom 
		 WHERE open_olcom_serv_code IN (
		 select olcom_serv_code 
                   from td_o_olcomservvar
                  WHERE olcom_var_CODE='G077'
    	 )
)