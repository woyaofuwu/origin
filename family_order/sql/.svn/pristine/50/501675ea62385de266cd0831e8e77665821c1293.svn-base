SELECT COUNT(1) recordcount
from tf_f_user_other
where rsrv_value_code='EcGb'
    and rsrv_str2=:RSRV_STR4
    and end_date>sysdate
    and start_date<end_date
    and exists (select 1 from td_s_commpara
                where SUBSYS_CODE='CSM'
                  and PARAM_ATTR=80
                  and PARAM_CODE=:RSRV_STR2
                  and PARA_CODE20='1'
                  and sysdate between start_date and end_date
                  and (eparchy_code=:EPARCHY_CODE or eparchy_code='ZZZZ'))