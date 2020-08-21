INSERT INTO tf_b_trade_discnt(trade_id,accept_month,id,discnt_code,modify_tag,start_date,end_date)
select TO_NUMBER(:TRADE_ID),to_number(to_char(sysdate,'MM')),user_id,'1',discnt_code,'1',
       start_date,TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS')-0.00001 
from tf_f_user_discnt a
where user_id=to_number(:ID)
  and PARTITION_ID=mod(to_number(:ID),10000)
  and end_date>=TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS')
  AND exists (select 1 from td_s_discnt_svc_limit b
                    where b.service_id = :SERVICE_ID
                      AND b.limit_tag = :LIMIT_TAG
                      AND (b.eparchy_code=:EPARCHY_CODE OR eparchy_code='ZZZZ')
                      AND b.discnt_code = to_char(a.discnt_code)
                      AND SYSDATE BETWEEN b.start_date AND b.end_date)