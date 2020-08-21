INSERT INTO tf_b_trade_svc(trade_id,accept_month,user_id,user_id_a,product_id,package_id,service_id,main_tag,
   inst_id,campn_id,start_date,end_date,modify_tag,update_time,update_staff_id,update_depart_id,remark)
SELECT to_number(:TRADE_ID), to_number(:ACCEPT_MONTH), to_number(:USER_ID), -1, -1, -1, to_number(:SERVICE_ID), '0',
    to_number(:INST_ID),null,start_date,TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS'),'1',sysdate,:TRADE_STAFF_ID,
    :TRADE_DEPART_ID,'优惠变化绑定服务变化'
  FROM tf_f_user_svc
WHERE user_id=TO_NUMBER(:USER_ID)
  AND partition_id=MOD(TO_NUMBER(:USER_ID), 10000)
  AND service_id=TO_NUMBER(:SERVICE_ID)
  AND end_date>SYSDATE
  AND NOT EXISTS(SELECT 1 FROM tf_b_trade_svc 
                   WHERE trade_id = TO_NUMBER(:TRADE_ID) 
                     AND accept_month = :ACCEPT_MONTH
                     AND service_id = TO_NUMBER(:SERVICE_ID)
                     AND modify_tag = '1')