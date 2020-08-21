select t.*, t.END_DATE-t.START_DATE DAYS, months_between(t.FIRST_DATE, t.START_DATE) DAYS2 from Tf_f_User_Acctday t 
   where t.USER_ID = TO_NUMBER(:USER_ID)
   and t.PARTITION_ID = MOD(TO_NUMBER(:USER_ID), 10000)
   and t.START_DATE < to_date(:MONTH, 'yyyy-mm-dd')
   and t.END_DATE >= last_day(to_date(:MONTH, 'yyyy-mm-dd'))
   and t.END_DATE > t.START_DATE
   order by t.end_date