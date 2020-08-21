select *
  from ucr_crm1.tf_f_user_discnt a
 where a.discnt_code in ('2000026',
                         '2000027',
                         '2000001',
                         '2000002',
                         '2000015',
                         '2000014',
                         '2000013',
                         '2000012',
                         '2000011',
                         '2000010',
                         '2000009',
                         '2000008',
                         '2000007',
                         '2000006',
                         '2000005',
                         '2000004',
                         '2000003')
   and a.end_date > sysdate
   and a.user_id = :USER_ID