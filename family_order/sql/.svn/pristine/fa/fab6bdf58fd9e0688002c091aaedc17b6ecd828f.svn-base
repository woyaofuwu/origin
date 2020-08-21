select COUNT(1) recordcount
from tf_b_trade_other a
where trade_id=to_number(:TRADE_ID)
  and RSRV_VALUE_CODE=:RSRV_VALUE_CODE
  and (rsrv_str1=:RSRV_STR1 or :RSRV_STR1='*')
  and (rsrv_str2=:RSRV_STR2 or :RSRV_STR2='*')
  and modify_tag=:MODIFY_TAG