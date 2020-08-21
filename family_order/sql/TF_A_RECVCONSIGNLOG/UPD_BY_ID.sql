UPDATE tf_a_recvconsignlog
   SET deal_tag='1',deal_time=sysdate,
       result_code=:RESULT_CODE,
      result_info=:RESULT_INFO,
      charge_id=:CHARGE_ID  
 WHERE consign_id=TO_NUMBER(:CONSIGN_ID)
   AND acyc_id=:ACYC_ID
   AND acct_id=TO_NUMBER(:ACCT_ID)
   AND act_tag=:ACT_TAG
   AND deal_tag='0'