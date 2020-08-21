SELECT MIN(b.bcyc_id)||'-'||MAX(b.bcyc_id) A,INTEGRAL_TYPE_CODE,SUM(integral_fee) INTEGRAL_FEE
FROM tf_ah_integralbill a,td_a_acycpara b
WHERE user_id=TO_NUMBER(:USER_ID) and partition_id=MOD(TO_NUMBER(:USER_ID),10000)
AND a.acyc_id=b.acyc_id
AND b.bcyc_id like NVL(:YEAR_ID,TO_CHAR(SYSDATE,'YYYY'))||'%'
GROUP BY integral_type_code