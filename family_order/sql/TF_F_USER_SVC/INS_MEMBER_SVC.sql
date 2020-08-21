INSERT INTO tf_f_user_svc(partition_id,user_id,service_id,main_tag,start_date,end_date,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,update_time)
SELECT TO_NUMBER(:PARTITION_ID),:USER_ID,service_id,'0',start_date,end_date,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,sysdate
 FROM tf_b_trade_svc
WHERE trade_id=:TRADE_ID
  AND accept_month=:ACCEPT_MONTH
  AND user_id=:USER_ID
  AND modify_tag='0'