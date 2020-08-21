INSERT INTO tf_b_trade_discnt(trade_id,accept_month,id,discnt_code,modify_tag,start_date,end_date)
SELECT trade_id,accept_month,id,b.discnt_code_b,a.modify_tag,a.start_date,a.end_date 
  FROM tf_b_trade_discnt a,td_s_discnt_limit b
 WHERE trade_id= TO_NUMBER(:TRADE_ID) AND accept_month= TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
   AND id_type='1' AND id=:USER_ID
   AND a.discnt_code=:DISCNT_CODE
   AND a.discnt_code=b.discnt_code_a AND a.modify_tag='1' AND b.limit_tag='5' --绑定删除
   AND (b.eparchy_code=:EPARCHY_CODE or b.eparchy_code = 'ZZZZ')
   AND NOT EXISTS(SELECT 1 FROM tf_b_trade_discnt 
                   WHERE trade_id=TO_NUMBER(:TRADE_ID) AND accept_month=TO_NUMBER(SUBSTR(:TRADE_ID,5,2))
                     AND discnt_code=b.discnt_code_b
                     AND modify_tag='1')
   AND EXISTS(SELECT 1 FROM tf_f_user_discnt 
                   WHERE partition_id=MOD(:USER_ID,10000) AND user_id=:USER_ID
                     AND discnt_code=b.discnt_code_b
                     AND end_date>sysdate)