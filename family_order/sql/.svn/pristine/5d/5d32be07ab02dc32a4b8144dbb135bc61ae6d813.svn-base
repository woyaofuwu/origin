 select t1.service_id,t1.main_tag,t1.state_code,t1.start_date,t1.end_date,t.*,t.rowid from tf_f_user t ,tf_F_user_svcstate t1 
  where t.user_id=t1.user_id
 and t.remove_tag='0'
 and sysdate < t1.end_date
  and t1.state_code not in ('6','9')
 and t.SERIAL_NUMBER=:KD_SERIAL_NUMBER