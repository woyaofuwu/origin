SELECT A.USER_ID,A.USER_ID_A,A.DISCNT_CODE,A.SPEC_TAG,A.INST_ID  FROM TF_F_USER_DISCNT A
WHERE A.USER_ID= TO_NUMBER( :USER_ID )
AND SYSDATE BETWEEN A.START_DATE AND A.END_DATE
AND A.PARTITION_ID=MOD(TO_NUMBER( :USER_ID ),10000)
