SELECT to_char(trade_id) trade_id,accept_month,to_char(user_id) user_id,service_id,serv_para1,serv_para2,serv_para3,serv_para4,serv_para5,serv_para6,serv_para7,serv_para8,modify_tag,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
from tf_b_trade_svc 
where user_id =TO_NUMBER(:USER_ID) and trunc(start_date,'mm')= TRUNC(SYSDATE,'mm')