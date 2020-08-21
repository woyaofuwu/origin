SELECT partition_id,to_char(user_id) user_id,to_char(fee) fee,sp_code,service_code,sp_name,oper_code,para_name,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,rsrv_info1,acyc_id,rsrv_info2 
FROM ts_a_sp_detailbill
WHERE partition_id=:PARTITION_ID
AND user_id=TO_NUMBER(:USER_ID)
AND acyc_id=:ACYC_ID
AND rownum<=10