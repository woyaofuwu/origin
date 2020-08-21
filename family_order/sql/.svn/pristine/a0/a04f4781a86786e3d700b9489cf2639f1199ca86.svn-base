INSERT INTO tf_b_trade_svc(trade_id,accept_month,user_id,user_id_a,product_id,package_id,service_id,main_tag,inst_id,campn_id,
start_date,end_date,modify_tag,update_time,update_staff_id,update_depart_id,remark,rsrv_num1,rsrv_num2,
rsrv_num3,rsrv_num4,rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,
rsrv_str8,rsrv_str9,rsrv_str10,rsrv_date1,rsrv_date2,rsrv_date3,rsrv_tag1,rsrv_tag2,rsrv_tag3
)
SELECT TO_NUMBER(:TRADE_ID),TO_NUMBER(SUBSTRB(:TRADE_ID,5,2)),TO_NUMBER(:USER_ID),user_id_a,product_id,package_id,service_id,main_tag,inst_id,campn_id,
start_date,sysdate-0.00001,'1',update_time,update_staff_id,update_depart_id,remark,rsrv_num1,rsrv_num2,
rsrv_num3,rsrv_num4,rsrv_num5,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,
rsrv_str8,rsrv_str9,rsrv_str10,rsrv_date1,rsrv_date2,rsrv_date3,rsrv_tag1,rsrv_tag2,rsrv_tag3
from tf_f_user_svc a
where user_id=to_number(:USER_ID)
 and partition_id=mod(to_number(:USER_ID),10000)
 and service_id in ('15','19')
 and sysdate BETWEEN start_date AND end_date