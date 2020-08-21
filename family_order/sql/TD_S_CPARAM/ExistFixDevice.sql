SELECT decode(COUNT(1),1,0,1) recordcount
  from TF_R_FIXEDDEVICE
 where device_state='1'
   and device_id in
   (
     select SERV_PARA1 device_id
     from TF_F_USER_SVC
     where user_id=:USER_ID
       and SERVICE_ID=1003
       AND end_date>=SYSDATE
   )