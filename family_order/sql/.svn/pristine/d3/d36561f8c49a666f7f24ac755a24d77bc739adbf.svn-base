SELECT COUNT(1) recordcount
  FROM (select uu.user_id_a
          from tf_f_relation_uu uu
         WHERE uu.user_id_a = TO_NUMBER(:USER_ID)
           AND uu.USER_ID_B = TO_NUMBER(:USER_ID_B)
           AND uu.PARTITION_ID = MOD(TO_NUMBER(:USER_ID_B), 10000)
            AND end_date + 0 > SYSDATE
           AND ROWNUM < 2
        UNION ALL
        select bb.user_id_a
           FROM tf_f_relation_bb bb
         WHERE bb.user_id_a = TO_NUMBER(:USER_ID)
           AND bb.USER_ID_B = TO_NUMBER(:USER_ID_B)
           AND bb.PARTITION_ID = MOD(TO_NUMBER(:USER_ID_B), 10000)
           AND end_date + 0 > SYSDATE
           AND ROWNUM < 2)