SELECT integral_type_code,score_type_name,TO_CHAR(NVL(integral_fee,0)) integral_fee,bcyc_id,TO_CHAR(acyc_start_time,'YYYYMMDD') start_date,TO_CHAR(acyc_end_time,'YYYYMMDD') end_date
FROM tf_ah_integralbill a,td_a_acycpara b,td_s_scoretype c
WHERE user_id=TO_NUMBER(:USER_ID)
AND partition_id=MOD(TO_NUMBER(:USER_ID),10000)
AND a.acyc_id=b.acyc_id
AND b.bcyc_id BETWEEN TO_NUMBER(:START_DATE)  AND TO_NUMBER(:END_DATE)
--AND integral_type_code IN ('1','2','3','9','0')
AND a.integral_type_code=c.score_type_code(+)
ORDER BY 1,3