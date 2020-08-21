SELECT :MONTH_BEGIN MONTH_BEGIN,:MONTH_END MONTH_END,INTEGRAL_TYPE_CODE,SUM(integral_fee) INTEGRAL_FEE
FROM tf_ah_integralbill a,td_a_acycpara b
WHERE user_id=TO_NUMBER(:USER_ID) and partition_id=MOD(TO_NUMBER(:USER_ID),10000)
AND a.acyc_id=b.acyc_id
AND b.BCYC_ID >= TO_NUMBER(:MONTH_BEGIN)
AND b.BCYC_ID <= TO_NUMBER(:MONTH_END)
GROUP BY integral_type_code