select a.service_id,a.start_date,a.end_date,b.start_date rsrv_date1,b.end_date rsrv_date2
          from tf_F_user_svc a, tf_F_user_svc b
         where A.user_id = TO_NUMBER(:USER_ID)
           AND A.partition_id = MOD(TO_NUMBER(:USER_ID), 10000)
           and a.user_id = b.user_id
           and a.service_id=b.service_id
           AND A.END_DATE > A.START_DATE
           AND A.END_DATE > SYSDATE
           AND b.END_DATE > b.START_DATE
           AND b.END_DATE > SYSDATE
           and a.start_date<b.end_date
           and a.end_date>b.start_date
           and a.inst_id<>b.inst_id
           and a.user_id_a='-1'
           and b.user_id_a='-1'