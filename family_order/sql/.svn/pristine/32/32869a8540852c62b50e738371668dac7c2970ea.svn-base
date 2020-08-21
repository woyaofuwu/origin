SELECT COUNT(1) recordcount FROM dual
WHERE (
SELECT SUM(recordnum) recordcount
FROM
(SELECT COUNT(*) recordnum
  FROM tf_f_user_discnt a
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND end_date >= sysdate
   AND NOT EXISTS (SELECT 1 FROM uop_jour1.tf_b_trade_discnt
                    WHERE trade_id = TO_NUMBER(:TRADE_ID)
                      AND accept_month = TO_NUMBER(:ACCEPT_MONTH)
                      AND a.user_id = TO_NUMBER(:USER_ID)
                      AND modify_tag = '1'
                      AND discnt_code = a.discnt_code)
   AND EXISTS(SELECT 1 FROM uop_cen1.td_s_commpara
              WHERE subsys_code='CSM'
                AND param_attr=2001
                AND param_code=:PARAM_CODE
                AND para_code1=to_char(a.discnt_code)
                AND (eparchy_code = :EPARCHY_CODE OR eparchy_code='ZZZZ')
                AND SYSDATE BETWEEN start_date AND end_date)
UNION ALL
SELECT COUNT(*) recordnum
  FROM uop_jour1.tf_b_trade_discnt b
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(:ACCEPT_MONTH)
   AND b.user_id = TO_NUMBER(:USER_ID)
   AND modify_tag = '0'
   AND EXISTS(SELECT 1 FROM uop_cen1.td_s_commpara
              WHERE subsys_code='CSM'
                AND param_attr=2001
                AND param_code=:PARAM_CODE
                AND para_code1=to_char(b.discnt_code)
                AND (eparchy_code = :EPARCHY_CODE OR eparchy_code='ZZZZ')
                AND SYSDATE BETWEEN start_date AND end_date)
)
)> :NUM