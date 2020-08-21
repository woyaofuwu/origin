SELECT to_char(trade_id) trade_id,accept_month,res_type_code,res_code,res_info1,res_info2,res_info3,res_info4,res_info5,res_info6,res_info7,res_info8,modify_tag,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  from tf_b_trade_res 
 where trade_id = :TRADE_ID
and accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   and modify_tag = '0'
   and res_type_code in 
   (select b.res_type_code
     from tf_b_trade_res b
     where b.trade_id = :TRADE_ID
and b.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
       and b.modify_tag = '1')