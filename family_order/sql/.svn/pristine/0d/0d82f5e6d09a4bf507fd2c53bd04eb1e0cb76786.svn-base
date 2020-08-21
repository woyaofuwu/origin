SELECT COUNT(1) recordcount
  FROM tf_f_user_discnt a
 WHERE partition_id = MOD(to_number(:USER_ID),10000)
   AND user_id=:USER_ID
   AND EXISTS(SELECT 1 FROM td_s_commpara
              WHERE subsys_code='CSM'
                AND param_attr=2001
                AND param_code=:PARAM_CODE
                AND para_code1=to_char(a.discnt_code)
                AND (eparchy_code = :EPARCHY_CODE OR eparchy_code='ZZZZ')
                AND SYSDATE BETWEEN start_date AND end_date)
   AND end_date>trunc(last_day(SYSDATE)+1)