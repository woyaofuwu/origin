UPDATE tf_f_user_discnt a 
 SET end_date=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')-0.00001,update_time=sysdate,update_staff_id=:UPDATE_STAFF_ID,update_depart_id=:UPDATE_DEPART_ID
  WHERE partition_id=mod(to_number(:USER_ID),10000)
   AND user_id=TO_NUMBER(:USER_ID)
   AND end_date>SYSDATE
   AND EXISTS(SELECT 1 FROM td_s_commpara
               WHERE param_attr = :PARAM_ATTR
                AND para_code2 = a.discnt_code
                AND para_code1 = :DISCNT_CODE
                AND (eparchy_code = :EPARCHY_CODE or eparchy_code = 'ZZZZ')
                AND sysdate BETWEEN start_date AND end_date)