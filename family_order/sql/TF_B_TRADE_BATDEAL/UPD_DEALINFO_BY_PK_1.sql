UPDATE tf_b_trade_batdeal
   SET exec_time = SYSDATE + TO_NUMBER(NVL(:OFFSET,600))/24/3600,
       deal_time = SYSDATE,
       deal_state = :DEAL_STATE,
       deal_result = F_SYS_ERRINFO_TRANS(:DEAL_RESULT,500),
       deal_desc = :DEAL_DESC
 WHERE operate_id = TO_NUMBER(:OPERATE_ID)