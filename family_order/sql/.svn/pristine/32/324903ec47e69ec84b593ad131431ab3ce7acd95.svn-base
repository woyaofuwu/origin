SELECT to_char(trade_id) trade_id,accept_month,res_type_code,res_code,res_info1,res_info2,res_info3,res_info4,res_info5,res_info6,res_info7,res_info8,modify_tag,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM tf_b_trade_res
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND res_type_code = :RES_TYPE_CODE