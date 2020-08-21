INSERT INTO tf_b_trade_discnt(trade_id,accept_month,id,discnt_code,modify_tag,start_date,end_date)
SELECT TO_NUMBER(:TRADE_ID),to_number(substrb(:TRADE_ID,5,2)),a.user_id,'1',a.discnt_code,'1',
       a.start_date,b.start_date-1/24/3600  FROM tf_f_user_discnt a,tf_b_trade_discnt b
 WHERE a.user_id=b.id
   AND b.trade_id=:TRADE_ID
   AND b.accept_month=to_number(substrb(:TRADE_ID,5,2))
   AND SYSDATE<a.end_date
   AND a.spec_tag=:SPEC_TAG
   AND (a.relation_type_code=:RELATION_TYPE_CODE OR :RELATION_TYPE_CODE IS NULL)
   AND b.modify_tag=:MODIFY_TAG
   AND EXISTS(SELECT 1 FROM td_s_discnt_limit c
               WHERE (c.discnt_code_a=a.discnt_code AND c.discnt_code_b=b.discnt_code
                     OR c.discnt_code_b=a.discnt_code AND c.discnt_code_a=b.discnt_code)
                 AND (c.eparchy_code=:EPARCHY_CODE OR c.eparchy_code='ZZZZ')
                 AND SYSDATE<c.end_date AND c.limit_tag=:LIMIT_TAG
                 AND (a.start_date BETWEEN b.start_date AND b.end_date
                     OR b.start_date BETWEEN a.start_date AND a.end_date))