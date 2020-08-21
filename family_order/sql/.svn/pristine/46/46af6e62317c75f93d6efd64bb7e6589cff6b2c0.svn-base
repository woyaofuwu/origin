SELECT count(1) recordcount
  FROM dual
 WHERE NVL(to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss'),SYSDATE) < DECODE(:TYPE,
              '0',to_date(:BEGIN_DATE,'yyyy-mm-dd hh24:mi:ss')+:NUM,                       --天
              '1',trunc(to_date(:BEGIN_DATE,'yyyy-mm-dd hh24:mi:ss')+:NUM),                --自然天
              '2',ADD_MONTHS(to_date(:BEGIN_DATE,'yyyy-mm-dd hh24:mi:ss'),:NUM),           --月
              '3',trunc(ADD_MONTHS(to_date(:BEGIN_DATE,'yyyy-mm-dd hh24:mi:ss'),:NUM),'mm'),    --自然月
              '4',ADD_MONTHS(to_date(:BEGIN_DATE,'yyyy-mm-dd hh24:mi:ss'),:NUM*12),        --年
              '5',trunc(ADD_MONTHS(to_date(:BEGIN_DATE,'yyyy-mm-dd hh24:mi:ss'),:NUM*12),'yy'), --自然年
              SYSDATE
              )