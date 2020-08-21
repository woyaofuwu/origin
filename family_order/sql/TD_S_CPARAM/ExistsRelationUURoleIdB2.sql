SELECT count(1) recordcount
FROM TF_F_RELATION_UU A
 WHERE A.partition_id=MOD(TO_NUMBER(:USER_ID_B),10000)
   AND A.user_id_b=TO_NUMBER(:USER_ID_B)
   AND A.relation_type_code=:RELATION_TYPE_CODE
   AND A.role_code_b=:ROLE_CODE_B
   AND A.end_date>SYSDATE