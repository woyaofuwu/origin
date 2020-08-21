SELECT t.start_date,t.rsrv_date1 RSRV_DATA1,t.rsrv_value,t.rsrv_str3 from tf_f_user_other t  
where partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
       AND user_id = TO_NUMBER(:USER_ID)
       AND rsrv_value_code = 'RIGHT_USE_RECORD'