select partition_id,user_id,service_id,serial_number,info_code,info_value,info_name,update_time,update_staff_id,
       update_depart_id,remark,rsrv_num1,rsrv_num2,rsrv_str1,rsrv_str2,rsrv_date1,rsrv_date2,rsrv_date3
from TF_F_USER_PLATSVC_ATTR a
where partition_id=MOD(TO_NUMBER(:USER_ID),10000)
 and user_id=TO_NUMBER(:USER_ID)
 and service_id=:SERVICE_ID
 and info_code=:INFO_CODE
 and exists (select 1 from TF_F_USER_PLATSVC
              where partition_id=a.partition_id
                and user_id=a.user_id
                and service_id=a.service_id
                and biz_type_code=:BIZ_TYPE_CODE
                and sysdate between start_date and end_date)