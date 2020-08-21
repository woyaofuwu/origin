INSERT INTO tf_b_trade_discnt(trade_id,accept_month,id,discnt_code,modify_tag,start_date,end_date)
select TO_NUMBER(:TRADE_ID),:ACCEPT_MONTH,TO_NUMBER(:ID),'1',a.discnt_code,:MODIFY_TAG,a.start_date,TRUNC(ADD_MONTHS(TO_DATE(:END_DATE,'YYYY-MM-DD HH24:MI:SS'),1),'mm')-1/24/3600
from tf_f_user_discnt a
where user_id=TO_NUMBER(:ID)
  and PARTITION_ID=mod(TO_NUMBER(:ID),10000)
  and sysdate<a.end_date
  and exists (select 1 from td_s_discnt_svc_limit b
                    where a.discnt_code=b.discnt_code
                      and b.limit_tag='2'
                      and b.service_id=:SERVICE_ID
                      and b.start_date<b.end_date
                      and b.eparchy_code=:EPARCHY_CODE)
  and not exists(select 1 from tf_b_trade_discnt c
                 where c.trade_id=TO_NUMBER(:TRADE_ID)
                   and a.discnt_code=c.discnt_code
                   and c.modify_tag=:MODIFY_TAG)