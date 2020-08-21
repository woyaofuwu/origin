insert into tf_f_user_mpute a
select MOD(:USER_ID,10000),:USER_ID,:MPUTE_TAG,DECODE(:MPUTE_TAG,'2',TRUNC(SYSDATE,'MM'),SYSDATE),:TRADE_ID,SYSDATE,to_date('20501231','yyyymmdd'),
       SYSDATE,'','','',NULL,NULL,NULL,NULL,NULL,:EPARCHY_CODE,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL
from dual
where not exists(select 1 from tf_f_user_mpute b
                 where b.partition_id = MOD(:USER_ID,10000)     
                 and b.user_id = :USER_ID
                 and b.mpute_month_fee = :MPUTE_TAG
                 and b.start_date = sysdate)