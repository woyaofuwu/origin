SELECT execute_id, execute_desc, serial_number, id_type, id, priority, description,
       to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,
       eparchy_code, to_char(update_date,'yyyy-mm-dd hh24:mi:ss') update_date, update_tag, update_staff_id,
       accept_month, rsrv_str1, rsrv_str2
  FROM tf_f_recommend a
 WHERE serial_number = :SERIAL_NUMBER
   AND sysdate between start_date+0 and end_date+0
   AND NOT EXISTS (SELECT 1 FROM tf_f_recommend_result
                    WHERE serial_number = a.serial_number
                      AND id_type = a.id_type+0
                      AND id = a.id+0
                      AND deal_tag = '2'
                      AND sysdate<deal_time+to_number(:TIME_AMONG_REFUSE))
   AND NOT EXISTS (SELECT 1 FROM tf_f_recommend_result
                    WHERE serial_number = a.serial_number
                      AND id_type = a.id_type+0
                      AND id = a.id+0
                      AND deal_tag = '0'
                      AND sysdate<deal_time+to_number(:TIME_AMONG_ACCEPT))
  AND NOT EXISTS (SELECT 1 FROM tf_f_user_svc b
                   WHERE b.user_id = :USER_ID
                     AND a.id_type = '1'
                     AND a.id = b.service_id+0
                     AND sysdate > b.start_date+0
                     AND sysdate < b.end_date+0)
 AND NOT EXISTS (SELECT 1 FROM tf_f_user_discnt c
                  WHERE c.user_id = :USER_ID
                    AND a.id_type = '2'
                    AND a.id = c.discnt_code+0
                    AND sysdate > c.start_date+0 
                    AND sysdate < c.end_date+0)
     ORDER BY priority DESC