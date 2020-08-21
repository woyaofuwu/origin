select t.* from Tf_f_User_Acctday t 
   where t.USER_ID = TO_NUMBER(:USER_ID)
   and t.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000)
   and t.START_DATE >= to_date(:MONTH, 'yyyy-mm-dd hh24:mi:ss')
   and t.START_DATE <= last_day(to_date(:MONTH, 'yyyy-mm-dd hh24:mi:ss'))
   and t.END_DATE > t.START_DATE
   order by t.end_date