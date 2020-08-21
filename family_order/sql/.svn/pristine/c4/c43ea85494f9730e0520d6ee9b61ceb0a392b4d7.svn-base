select *
  from ucr_crm1.tf_f_user_discnt t
 where t.discnt_code ='2000026'
   and t.end_date > sysdate
   and t.user_id = :USER_ID
   and not exists (select 1
          from tf_f_user_platsvc s
         where t.user_id = s.user_id
           and s.service_id = '40227788'
           and s.biz_state_code='A'
           and sysdate between s.start_date and s.end_date)