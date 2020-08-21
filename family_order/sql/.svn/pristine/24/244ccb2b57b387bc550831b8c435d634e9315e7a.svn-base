SELECT COUNT(1) recordcount FROM dual
WHERE (
SELECT COUNT(1)  FROM tf_b_trade_svc a
 WHERE trade_id=TO_NUMBER(:TRADE_ID)
   AND accept_month=TO_NUMBER(:ACCEPT_MONTH)
   AND (decode(modify_tag, '4', '0','5','1', modify_tag)=:MODIFY_TAG OR :MODIFY_TAG = '*')
   AND EXISTS (SELECT 1 FROM td_s_commpara
              WHERE subsys_code='CSM'
                AND param_attr=2001
                AND param_code=:PARAM_CODE
                AND para_code1=to_char(a.service_id)
                AND (eparchy_code = :EPARCHY_CODE OR eparchy_code='ZZZZ')
                AND SYSDATE BETWEEN start_date AND end_date)
       ) > :NUM