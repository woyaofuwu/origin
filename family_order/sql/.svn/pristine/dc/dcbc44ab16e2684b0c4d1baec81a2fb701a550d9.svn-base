INSERT INTO tf_b_trade_svc(trade_id,accept_month,user_id,user_id_a,product_id,package_id,service_id,main_tag,
   inst_id,campn_id,start_date,end_date,modify_tag,update_time,update_staff_id,update_depart_id,remark)
VALUES (to_number(:TRADE_ID), to_number(substr(:TRADE_ID,5,2)), to_number(:USER_ID), to_number(:USER_ID_A),
  :PRODUCT_ID,:PACKAGE_ID,:SERVICE_ID,:MAIN_TAG,to_number(:INST_ID),to_number(:CAMPN_ID),
  TO_DATE(:START_DATE,'YYYY-MM-DD HH24:MI:SS'),TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS'),
  :MODIFY_TAG,sysdate,:UPDATE_STAFF_ID,:UPDATE_DEPART_ID,'预约产品变更后服务变更特殊增加')