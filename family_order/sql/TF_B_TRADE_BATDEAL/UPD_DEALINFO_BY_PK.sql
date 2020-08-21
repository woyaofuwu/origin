UPDATE tf_b_trade_batdeal
   SET deal_time = SYSDATE,
       deal_state = :DEAL_STATE,
       deal_result = F_SYS_ERRINFO_TRANS(:DEAL_RESULT,500),
       deal_desc = :DEAL_DESC,
       trade_id = NVL(TO_NUMBER(:TRADE_ID),trade_id)
 WHERE operate_id = TO_NUMBER(:OPERATE_ID)