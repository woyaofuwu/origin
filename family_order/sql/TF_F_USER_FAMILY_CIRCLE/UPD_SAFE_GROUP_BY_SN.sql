update tf_f_user_family_circle t 
set t.target_name = :TARGET_NAME , 
t.target_role = :TARGET_ROLE ,
t.group_area = :GROUP_AREA ,
t.status = :STATUS , 
t.END_DATE = to_date('20501230235959','YYYYMMDDHH24MISS'),
t.start_date = sysdate 
where t.target_msisdn = :TARGET_MSISDN and 
t.group_code = :GROUP_CODE and 
t.group_type = :GROUP_TYPE
