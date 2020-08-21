DELETE FROM tf_f_user_discnt
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND (relation_type_code IS NULL OR relation_type_code NOT IN (SELECT relation_type_code FROM td_s_relation WHERE relation_kind = 'F')) --排除亲情关系
   AND (end_date>SYSDATE OR start_date=TRUNC(SYSDATE))