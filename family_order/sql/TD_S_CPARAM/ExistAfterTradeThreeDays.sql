SELECT COUNT(1) recordcount
FROM tf_bh_trade a 
WHERE a.User_id=to_number(:USER_ID)
AND a.trade_type_code=:TRADE_TYPE_CODE 
AND a.IN_MODE_CODE=:IN_MODE_CODE
AND cancel_tag=:CANCEL_TAG 
AND trunc(sysdate)-trunc(finish_date)<4
AND NOT EXISTS(
  SELECT 1 FROM tf_bh_trade B WHERE A.Serial_Number=B.Serial_Number AND B.TRADE_TYPE_CODE='149' AND B.IN_MODE_CODE='TN' AND TRUNC(B.ACCEPT_DATE)=trunc(A.ACCEPT_DATE)
)