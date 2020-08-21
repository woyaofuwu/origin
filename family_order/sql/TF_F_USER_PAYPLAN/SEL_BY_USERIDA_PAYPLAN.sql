SELECT A.partition_id,to_char(A.user_id_a) user_id_a,A.serial_number_a,to_char(A.user_id_b) user_id_b,A.serial_number_b,A.relation_type_code,A.role_code_a,A.role_code_b,A.orderno,A.short_code,to_char(A.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(A.end_date,'yyyy-mm-dd hh24:mi:ss') end_date
  FROM TF_F_RELATION_UU A,TF_F_USER_PAYPLAN B
WHERE A.USER_ID_A= :USER_ID
AND A.RELATION_TYPE_CODE = :RELATION_TYPE_CODE
AND sysdate BETWEEN A.START_DATE AND A.END_DATE
AND B.USER_ID_A = :USER_ID
AND B.USER_ID = A.USER_ID_B
AND B.PARTITION_ID=MOD(TO_NUMBER(A.USER_ID_B),10000)
AND B.PLAN_TYPE_CODE= :PLAN_TYPE_CODE
AND B.START_DATE < SYSDATE
AND B.END_DATE > SYSDATE