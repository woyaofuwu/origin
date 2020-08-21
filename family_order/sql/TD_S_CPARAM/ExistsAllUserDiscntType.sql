SELECT SUM(recordnum) recordcount
FROM
(SELECT COUNT(*) recordnum
  FROM tf_f_user_discnt a
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND end_date >= ADD_MONTHS(TRUNC(SYSDATE,'MM'),1)
   AND NOT EXISTS (SELECT 1 FROM tf_b_trade_discnt
                    WHERE trade_id = TO_NUMBER(:TRADE_ID)
                      AND accept_month = TO_NUMBER(:ACCEPT_MONTH)
                      AND a.user_id = TO_NUMBER(:USER_ID)
                      AND modify_tag = '1'
                      AND discnt_code = a.discnt_code)
   AND EXISTS (SELECT 1 FROM td_b_dtype_discnt
                WHERE discnt_code = a.discnt_code
                  AND discnt_type_code = :DISCNT_TYPE_CODE)
UNION ALL
SELECT COUNT(*) recordnum
  FROM tf_b_trade_discnt b
 WHERE trade_id = TO_NUMBER(:TRADE_ID)
   AND accept_month = TO_NUMBER(:ACCEPT_MONTH)
   AND b.user_id = TO_NUMBER(:USER_ID)
   AND b.end_date >= ADD_MONTHS(TRUNC(SYSDATE,'MM'),1)
   AND modify_tag in('0', 'U')
   AND EXISTS (SELECT 1 FROM td_b_dtype_discnt
                WHERE discnt_code = b.discnt_code
                  AND discnt_type_code = :DISCNT_TYPE_CODE)
UNION ALL
SELECT COUNT(*) recordnum
  FROM tf_f_user_discnt a
 WHERE user_id = TO_NUMBER(:USER_ID)
   AND partition_id = MOD(TO_NUMBER(:USER_ID),10000)
   AND end_date >= SYSDATE
   AND EXISTS (SELECT 1 FROM tf_b_trade_discnt
                    WHERE trade_id = TO_NUMBER(:TRADE_ID)
                      AND accept_month = TO_NUMBER(:ACCEPT_MONTH)
                      AND a.user_id = TO_NUMBER(:USER_ID)
                      AND modify_tag = '2'
                      AND end_date >= ADD_MONTHS(TRUNC(SYSDATE,'MM'),1)
                      AND discnt_code = a.discnt_code)
   AND EXISTS (SELECT 1 FROM td_b_dtype_discnt
                WHERE discnt_code = a.discnt_code
                  AND discnt_type_code = :DISCNT_TYPE_CODE)
)