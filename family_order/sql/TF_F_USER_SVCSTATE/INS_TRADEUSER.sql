INSERT INTO tf_f_user_svcstate(partition_id,user_id,service_id,main_tag,state_code,start_date,end_date,update_time)
 SELECT TO_NUMBER(:PARTITION_ID),:USER_ID,a.service_id,b.main_tag,:STATE_CODE,sysdate,a.end_date,SYSDATE
 from tf_b_trade_svc a,td_b_package_element b,td_b_product_package c
 where a.trade_id = :TRADE_ID
 and a.user_id = :USER_ID
 and c.product_id=:PRODUCT_ID
 and a.service_id = b.element_id
 and b.PACKAGE_ID=c.PACKAGE_ID
 and a.modify_tag = '0'
 and b.start_date <= sysdate
 and b.end_date >= sysdate