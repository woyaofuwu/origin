UPDATE TF_B_RESAPPLY_MAIN
   SET audit_state_code = '2',
       audit_type_code = :AUDIT_TYPE_CODE   
 WHERE apply_no = :APPLY_NO
   AND (APPLY_TYPE_CODE = :APPLY_TYPE_CODE)
   AND (:AUDIT_STATE_CODE is null or audit_state_code = :AUDIT_STATE_CODE)   
   AND (:AUDIT_STATE_OLD is null OR audit_state_code = :AUDIT_STATE_OLD)