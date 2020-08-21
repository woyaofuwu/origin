UPDATE tf_f_user_discnt a
   SET a.user_id_a = TO_NUMBER(:USER_ID_A),
       a.remark    = decode(a.user_id_a,
                            '-1',
                            '欠费销号时补user_id_a',
                            a.remark)
 WHERE discnt_code in ('3403', '3404', '3405', '3406')
   AND end_date > SYSDATE
   and exists (select 1
          from tf_f_relation_uu b
         where b.partition_id = a.partition_id
           and b.user_id_b = a.user_id
           and b.user_id_a = TO_NUMBER(:USER_ID_A)
           and b.end_Date > sysdate)