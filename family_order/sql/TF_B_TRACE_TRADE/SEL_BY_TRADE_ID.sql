select X_TRACE_ID,X_PARENT_TRACE_ID,TRADE_ID,ACCEPT_MONTH,CANCEL_TAG
from tf_b_trace_trade a
 WHERE a.trade_id = TO_NUMBER(:TRADE_ID)
   AND a.accept_month = TO_NUMBER(:ACCEPT_MONTH)
   AND a.cancel_tag = :CANCEL_TAG