SELECT 
pre_id,
pre_type,
accept_month,
accept_state,
request_id,
order_id,
start_date,
end_date,
trade_type_code,
serial_number,
reply_state,
reply_time,
reply_content,
accept_result,
accept_data1,
accept_data2,
accept_data3,
accept_data4,
accept_data5,
remark,
svc_name,
rsrv_str1,
rsrv_str2,
rsrv_str3,
rsrv_str4,
rsrv_str5
FROM TF_B_ORDER_PRE T 
WHERE 1=1
and T.trade_type_code=:trade_type_code
and T.pre_type =:pre_type
and T.accept_state =  :accept_state
