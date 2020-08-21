select case
         when to_char(max(t.end_date), 'yyyymm') =
              to_char(sysdate, 'yyyymm') then
          1
         else
          0
       end recordcount
  from TF_F_USER_DISCNT t
 where t.user_id = to_number(:USER_ID)
   AND t.partition_id = mod(to_number(:USER_ID), 10000)