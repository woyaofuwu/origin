SELECT COUNT(1) recordcount
  FROM tf_f_user_other a
 WHERE partition_id=MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND rsrv_value_code=:RSRV_VALUE_CODE
   and end_date > sysdate
   AND EXISTS(SELECT 1 FROM td_s_commpara
              WHERE subsys_code='CSM'
                AND param_attr=2001
                AND param_code= :PARAM_CODE
                AND para_code1= a.rsrv_str2
                AND (eparchy_code = :EPARCHY_CODE OR eparchy_code='ZZZZ')
                AND SYSDATE BETWEEN start_date AND end_date)
   AND :RSRV_STR1 in (select para_code1 from td_s_commpara where subsys_code = 'CSM' and param_attr = 2001 and param_code=:PARAM_CODE1 and (eparchy_code = :EPARCHY_CODE OR eparchy_code='ZZZZ') and sysdate between start_date and end_date)