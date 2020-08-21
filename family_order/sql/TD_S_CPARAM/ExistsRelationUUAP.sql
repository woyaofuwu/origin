SELECT COUNT(1) recordcount
  FROM (
 select uu.user_id_b from  
  tf_f_relation_uu uu,tf_f_user u
 WHERE uu.user_id_a = TO_NUMBER(:USER_ID)
   AND uu.user_id_b = u.user_id
   AND u.remove_tag = '0'
   AND NOT (uu.RELATION_TYPE_CODE = '25' AND uu.ROLE_CODE_B IN (2,9))
   AND UU.RELATION_TYPE_CODE NOT IN ('F2','F3','F4')
   AND end_date + 0 > SYSDATE
   AND ROWNUM =1
   UNION ALL
   select bb.user_id_b from  
  tf_f_relation_bb bb,tf_f_user u
 WHERE bb.user_id_a = TO_NUMBER(:USER_ID)
   AND bb.user_id_b = u.user_id
   AND u.remove_tag = '0'
   AND NOT (bb.RELATION_TYPE_CODE = '90' )
   AND end_date + 0 > SYSDATE
   AND ROWNUM =1
   )   