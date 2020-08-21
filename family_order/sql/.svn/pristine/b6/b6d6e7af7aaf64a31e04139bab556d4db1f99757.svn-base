select b.partition_id,to_char(b.user_id) user_id,to_char(b.user_id_a) user_id_a,b.service_id,b.main_tag,b.inst_id,
b.campn_id,to_char(b.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(b.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,
to_char(b.update_time,'yyyy-mm-dd hh24:mi:ss') update_time,b.update_staff_id,b.update_depart_id,b.remark,
b.rsrv_num1,b.rsrv_num2,b.rsrv_num3,b.rsrv_num4,b.rsrv_num5,b.rsrv_str1,b.rsrv_str2,b.rsrv_str3,b.rsrv_str4,b.rsrv_str5,b.rsrv_str6,b.rsrv_str7,b.rsrv_str8,b.rsrv_str9,b.rsrv_str10,b.rsrv_date1,b.rsrv_date2,b.rsrv_date3,b.rsrv_tag1,b.rsrv_tag2,b.rsrv_tag3 
from tf_f_user_product a, tf_f_user_svc b
where b.user_id=:USER_ID
and b.partition_id=MOD(TO_NUMBER(:USER_ID),10000)
and b.user_id_a = a.user_id_a
and sysdate between b.start_date and b.end_date
and a.user_id=:USER_ID
and a.partition_id=MOD(TO_NUMBER(:USER_ID),10000)
and a.product_id in ('20005014','20005016','20171215')
and sysdate between a.start_date and a.end_date