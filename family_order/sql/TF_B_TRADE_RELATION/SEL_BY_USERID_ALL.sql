SELECT to_char(trade_id) trade_id,accept_month,relation_attr,relation_type_code,to_char(id_a) id_a,to_char(id_b) id_b,role_code_a,role_code_b,orderno,short_code,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,modify_tag,remark 
  FROM tf_b_trade_relation a
 WHERE EXISTS(SELECT 1 FROM tf_b_trade WHERE trade_id=a.trade_id AND cancel_tag='0' AND trade_type_code<5000
             AND exec_time>=last_day(trunc(SYSDATE))+1)
   AND (id_a=TO_NUMBER(:USER_ID)
   OR id_b=TO_NUMBER(:USER_ID))
   AND relation_attr='0'
   AND a.modify_tag='0'