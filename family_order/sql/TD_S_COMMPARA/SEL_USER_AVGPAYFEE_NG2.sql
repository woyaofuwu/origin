select ROUND(NVL(MAX(NVL(AVG(fee),0)),0)/100,2) PARAM_CODE from  tf_o_credit_userbill x 
  where x.user_id=:USER_ID 
  and x.partition_id = MOD(TO_NUMBER(:USER_ID), 10000) 
  and x.cycle_id >= TO_NUMBER(TO_CHAR(ADD_MONTHS(SYSDATE,- TO_NUMBER(:MONTHS_NUM)),'yyyymm'))
  and x.cycle_id <= TO_NUMBER(TO_CHAR(ADD_MONTHS(SYSDATE,-1),'yyyymm'))
  GROUP BY x.user_id