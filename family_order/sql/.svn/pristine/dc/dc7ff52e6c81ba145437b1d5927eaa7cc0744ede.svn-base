SELECT COUNT(1) recordcount
  FROM (
        select bb.user_id_a
           FROM tf_f_relation_bb bb
         WHERE bb.relation_type_code =:RELATION_TYPE_CODE
           AND bb.USER_ID_B = TO_NUMBER(:USER_ID_B)
           AND bb.PARTITION_ID = MOD(TO_NUMBER(:USER_ID_B), 10000)
           AND :CHECK_TAG = -1
           AND end_date + 0 > SYSDATE
           AND ROWNUM < 2)
