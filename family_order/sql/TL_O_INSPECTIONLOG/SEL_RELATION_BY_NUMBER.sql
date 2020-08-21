SELECT :TASK_ID             TASKE_ID,
        :SEARCHNUMBER        SEARCHNUMBER,
        c.relation_type_code BINDTYPE,
        c.serial_number_a    BINDNUMBER
FROM TF_F_USER A, TF_F_RELATION_UU C
WHERE a.serial_number = :SEARCHNUMBER
AND a.remove_tag = '0'
AND c.partition_id = MOD(A.USER_ID, 10000)
AND C.USER_ID_B = A.USER_ID
AND C.Relation_Type_Code in ('45', '75')
AND c.end_date >SYSDATE