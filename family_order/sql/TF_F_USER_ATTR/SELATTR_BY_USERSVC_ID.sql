SELECT partition_id,to_char(user_id) user_id,inst_type,inst_id,attr_code,attr_value,  to_char(start_date, 'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(end_date, 'yyyy-mm-dd hh24:mi:ss') end_date,
       to_char(update_time, 'yyyy-mm-dd hh24:mi:ss') update_time,update_depart_id,remark,rsrv_num1  FROM tf_f_user_attr t  
    WHERE user_id = TO_NUMBER(:USER_ID)
   AND rsrv_num1=:SERVICE_ID
   AND partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
   AND SYSDATE BETWEEN start_date AND end_date