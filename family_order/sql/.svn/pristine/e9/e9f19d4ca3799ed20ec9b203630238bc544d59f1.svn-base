SELECT trunc(sysdate, 'dd') -
       trunc(to_date((SELECT to_char(open_date, 'yyyy-mm-dd')
                       FROM TF_F_USER
                      WHERE user_id = TO_NUMBER(:USER_ID)
                        AND partition_id = MOD(TO_NUMBER(:USER_ID), 10000)),
                     'yyyy-mm-dd'),
             'dd') OUTSTR
  FROM DUAL