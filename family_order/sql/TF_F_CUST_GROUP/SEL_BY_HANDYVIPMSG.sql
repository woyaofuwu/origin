SELECT cust_name,serial_number group_contact_phone,vip_type_code group_attr,vip_class_id class_id,msg_body prevaluec1,stat_date prevalue1,DECODE(remind_id,'VIPDA','生日提醒','VIPFW','呼转提醒','VIPZT','状态变更') prevalue2
FROM v_ts_s_msg_vip
 WHERE stat_date>=TRIM(:START_DATE)
AND stat_date<=TRIM(:END_DATE)
AND remind_id=:REMIND_ID
AND cust_manager_id=:MANAGER_ID