SELECT COUNT(1) recordcount
  FROM tf_f_relation_uu uu
 WHERE user_id_a = TO_NUMBER(:USER_ID)
   AND uu.RELATION_TYPE_CODE = 41
   AND NOT (uu.RELATION_TYPE_CODE = 25 AND uu.ROLE_CODE_B IN (2,9))
   AND end_date + 0 > SYSDATE
   AND ROWNUM <  2