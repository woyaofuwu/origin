select a.service_id,a.attr_code,a.attr_name, 
       (select b.info_value from TF_F_USER_PLATSVC_ATTR b
        where a.attr_code=b.info_code and
              b.service_id=:SERVICE_ID and
              b.user_id=:USER_ID and
              b.partition_id = mod(:USER_ID,10000)
       ) attr_value,a.rsrv_str2
From TD_B_PLATSVC_ATTR a
where a.service_id=:SERVICE_ID
order by a.RSRV_STR1