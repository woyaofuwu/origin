select t.partition_id,t.user_id,t.service_id,t.serial_number,t.oper_code,t.info_code,t.info_value,t.info_name,
			 t.update_time,t.update_staff_id,t.update_depart_id,t.remark,
       t.rsrv_num1,t.rsrv_num2,t.rsrv_str1,t.rsrv_str2,t.rsrv_date1,t.rsrv_date2,t.rsrv_date3
from tf_f_user_platsvc_attr t 
where service_id = '3005' 
and info_code in ('3001','PASSWD')
and t.user_id = :USER_ID  
and partition_id=mod(:USER_ID,10000)