SELECT COUNT(1) recordcount
FROM tf_f_relation_uu
WHERE user_id_b = :USER_ID
AND relation_type_code = :RELATION_TYPE_CODE
AND role_code_b = :ROLE_CODE_B
AND sysdate BETWEEN start_date AND end_date