insert into tf_f_user_svcstate(partition_id,user_id,service_id,main_tag,state_code,start_date,end_date,update_time,update_staff_id,update_depart_id)
select partition_id,user_id,service_id,'1','0',end_date+0.00001,to_date('2050-12-31 23:59:59','YYYY-MM-DD HH24:MI:SS'),SYSDATE,:UPDATE_STAFF_ID,:UPDATE_DEPART_ID
from tf_f_user_svcstate
where partition_id = mod(TO_NUMBER(:USER_ID),10000)
    and user_id=TO_NUMBER(:USER_ID)
    and main_tag='1'
    and start_date=(select max(start_date) from tf_f_user_svcstate
                    where partition_id = mod(TO_NUMBER(:USER_ID),10000)
                        and user_id=TO_NUMBER(:USER_ID)
                        and main_tag='1'
                        and sysdate>=end_date)