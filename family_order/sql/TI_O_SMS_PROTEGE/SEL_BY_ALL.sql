select recv_id,channel_id,serial_no,prov_id,access_no,expire_date,
 accept_time,create_staff_id,update_time,status,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5 from TI_O_SMS_PROTEGE t
where ACCESS_NO=:ACCESS_NO and SERIAL_NO=:SERIAL_NO and status=0