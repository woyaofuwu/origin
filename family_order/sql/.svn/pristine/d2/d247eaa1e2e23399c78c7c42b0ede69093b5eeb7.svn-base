update tf_f_user_other t
set t.rsrv_str5='B'
where t.user_id=:USER_ID_CPE
and t.rsrv_value_code='CPE_DEVICE'
and sysdate < t.end_date
and t.start_date < t.end_date