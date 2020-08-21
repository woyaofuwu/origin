DELETE tf_b_trade_discnt a
 WHERE trade_id= TO_NUMBER(:TRADE_ID) AND accept_month= TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND id_type='1' AND id=:USER_ID
   AND a.discnt_code IN (SELECT discnt_code_b FROM td_s_discnt_limit 
                          WHERE discnt_code_a=:DISCNT_CODE AND limit_tag='4' --绑定增加
                            AND (eparchy_code=:EPARCHY_CODE OR eparchy_code = 'ZZZZ'))
   AND a.modify_tag='1'