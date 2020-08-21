UPDATE tf_b_trade_batdeal
   SET deal_state = :DEAL_STATE,deal_time = SYSDATE
 WHERE operate_id = TO_NUMBER(:DEAL_ID)
   AND cancel_tag = :DEAL_TAG
   AND (:DEAL_EPARCHY_CODE IS NULL OR :DEAL_EPARCHY_CODE = :DEAL_EPARCHY_CODE)