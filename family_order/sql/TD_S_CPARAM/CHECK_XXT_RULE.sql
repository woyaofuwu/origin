SELECT COUNT(*) RECORDCOUNT FROM tf_f_user r,tf_f_user_platsvc p
 WHERE  r.user_id = p.user_id
 and r.serial_number=:SERIAL_NUMBER
 and p.service_id ='55609175'
 and r.remove_tag = '0'
 and p.end_date>sysdate