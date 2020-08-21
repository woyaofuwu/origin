UPDATE tf_f_user_discnt a
   SET a.end_date=TRUNC(LAST_DAY(TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS')))+1-1/24/3600
 WHERE a.user_id = TO_NUMBER(:USER_ID)
   AND a.partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND SYSDATE < a.end_date+0
   AND discnt_code in (select to_number(param_code) from td_s_commpara where param_attr='348'
                    and end_date>SYSDATE)