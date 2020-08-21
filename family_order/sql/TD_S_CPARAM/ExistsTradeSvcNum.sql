SELECT COUNT(1) recordcount FROM dual
WHERE (
SELECT COUNT(1)  FROM tf_b_trade_svc
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month=TO_NUMBER(:ACCEPT_MONTH)
   AND (decode(modify_tag, '4', '0','5','1', modify_tag)=:MODIFY_TAG OR :MODIFY_TAG = '*')) > :NUM