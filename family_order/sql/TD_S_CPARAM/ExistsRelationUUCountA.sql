SELECT COUNT(*) RECORDCOUNT
  FROM tf_f_relation_uu a
 WHERE a.relation_type_code = '75'
   AND end_date > SYSDATE
   AND a.role_code_b = '1'   
   And user_id_b = :USER_ID 
   And partition_id = to_number(Mod(:USER_ID,10000))
   AND EXISTS (SELECT 1
          FROM tf_f_relation_uu
         WHERE user_id_a = a.user_id_a
           AND role_code_b <> '1' And relation_type_code = :RELATION_TYPE_CODE
           AND end_date > SYSDATE)