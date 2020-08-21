select ROUND(NVL(MAX(NVL(AVG(fee),0)),0)/100,2) PARAM_CODE from  tf_o_credit_userbill x ,
( select max(cycle_id) MAX_CYC , min(cycle_id) MIN_CYC from td_b_cycle b where
  ( ( add_months(SYSDATE,-1) BETWEEN b.CYC_START_TIME AND b.CYC_END_TIME )
  or( add_months(SYSDATE,-1- TO_NUMBER(:MONTHS_NUM)) BETWEEN b.CYC_START_TIME AND b.CYC_END_TIME ) ) ) y
  where x.user_id=:USER_ID 
  and x.partition_id = MOD(TO_NUMBER(:USER_ID), 10000) 
  and x.cycle_id >= y.MIN_CYC
  and x.cycle_id <= y.MAX_CYC
  GROUP BY x.user_id