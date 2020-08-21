SELECT count(1) recordcount
  FROM dual
 WHERE NVL(to_date(:END_DATE,'yyyy-mm-dd hh24:mi:ss'),SYSDATE) > DECODE(:TYPE,
              '0',(select in_date from tf_f_user where user_id=:USER_ID)+:NUM,                       --天
              '1',trunc((select in_date from tf_f_user where user_id=:USER_ID)+:NUM),                --自然天
              '2',ADD_MONTHS((select in_date from tf_f_user where user_id=:USER_ID),:NUM),           --月
              '3',trunc(ADD_MONTHS((select in_date from tf_f_user where user_id=:USER_ID),:NUM),'mm'),    --自然月
              '4',ADD_MONTHS((select in_date from tf_f_user where user_id=:USER_ID),:NUM*12),        --年
              '5',trunc(ADD_MONTHS((select in_date from tf_f_user where user_id=:USER_ID),:NUM*12),'yy'), --自然年
              SYSDATE
              )