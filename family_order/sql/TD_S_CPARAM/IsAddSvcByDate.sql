SELECT count(1) recordcount
  FROM tf_f_user_svc a
 WHERE  a.partition_id = mod(to_number(:USER_ID),10000)
        and a.user_id = to_number(:USER_ID)
        and a.service_id = to_number(:SERVICE_ID)
                and EXISTS (SELECT 1 FROM ucr_crm1.tf_b_trade_svc
                    where  trade_id=TO_NUMBER(:TRADE_ID)
                    AND accept_month=TO_NUMBER(:ACCEPT_MONTH)
                    AND service_id=a.service_id
                    AND (modify_tag=:MODIFY_TAG OR :MODIFY_TAG = '*')
                    )
        and SYSDATE < DECODE(:TYPE,
              '0',a.start_date+:NUM,                            --天
              '1',trunc(a.start_date)+:NUM,                    --自然天
              '2',ADD_MONTHS(a.start_date,:NUM),               --月
              '3',trunc(ADD_MONTHS(a.start_date,:NUM),'mm'),    --自然月
              '4',ADD_MONTHS(a.start_date,:NUM*12),            --年
              '5',trunc(ADD_MONTHS(a.start_date,:NUM*12),'yy'), --自然年
               SYSDATE
              )