SELECT /*+INDEX (a idx_tf_b_trade_exectime) INDEX(b pk_tf_b_trade_res)*/ to_char(b.trade_id) trade_id,b.accept_month,b.res_type_code,b.res_code,b.res_info1,b.res_info2,b.res_info3,b.res_info4,b.res_info5,b.res_info6,
b.res_info7,b.res_info8,b.modify_tag,to_char(b.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(b.end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
FROM tf_b_trade a , tf_b_trade_res b
WHERE a.exec_time>SYSDATE-7 AND a.rsrv_str1=:USER_ID_A
   AND cancel_tag='0' AND a.trade_id=b.trade_id AND a.accept_month=b.accept_month
   AND  res_type_code=:RES_TYPE_CODE
   AND res_code=:RES_CODE
   AND res_info8=:USER_ID_A