INSERT INTO tf_b_trade_discnt(trade_id,accept_month,id,discnt_code,modify_tag,start_date,end_date)
SELECT TO_NUMBER(:TRADE_ID),to_number(substrb(:TRADE_ID,5,2)),a.user_id,'1',a.discnt_code,'1',
       a.start_date,trunc(b.start_date)-1/24/3600
  FROM tf_f_user_discnt a,tf_b_trade_product b
 WHERE a.user_id=b.user_id
   AND b.trade_id=:TRADE_ID
   AND b.accept_month=to_number(substrb(:TRADE_ID,5,2))
   AND SYSDATE<a.end_date
   AND a.spec_tag=:SPEC_TAG
   AND a.relation_type_code=:RELATION_TYPE_CODE
   AND b.modify_tag=:MODIFY_TAG
   AND NOT EXISTS (SELECT 1 FROM td_s_commpara 
                    WHERE subsys_code = 'CSM'
                      AND param_attr = 6018
                      AND param_code = to_number(a.discnt_code )
                      AND sysdate between start_date and end_date
                   )