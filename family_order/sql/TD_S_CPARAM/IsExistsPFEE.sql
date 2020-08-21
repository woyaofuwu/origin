select count(1) recordcount from dual
where :USER_ID in(
select rsrv_str2 from tf_f_user_other where rsrv_value_code = 'PFEE' and end_date>sysdate)