SELECT COUNT(1) recordcount
from tf_f_user a
where partition_id=mod(to_number(:USER_ID),10000)
    and user_id=to_number(:USER_ID)
    and exists (select 1 from td_s_commpara
                    where SUBSYS_CODE='CSM'
                      and PARAM_ATTR=259
                      and PARAM_CODE=to_char(:PRODUCT_ID)
                      and para_code1=a.brand_code
                      and sysdate between start_date and end_date
                      and (eparchy_code=:EPARCHY_CODE or eparchy_code='ZZZZ'))