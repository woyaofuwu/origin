delete tf_f_user_platsvc
 where partition_id = mod(:USER_ID, 10000)
   and user_id = :USER_ID
   and service_id = :SERVICE_ID
   and ORG_DOMAIN = 'CMMB'
   and start_date > last_day(sysdate)
   and oper_code not in('07','02')