SELECT trunc(m.finish_time) finish_time,
       COUNT(CASE WHEN m.reason_code=1 THEN 1 ELSE null END) rsrv_str1,
       COUNT(CASE WHEN m.reason_code=2 THEN 1 ELSE null END) rsrv_str2,
       COUNT(CASE WHEN m.reason_code=2 AND EXISTS (SELECT 1 FROM tf_o_trashmsguser t
                                                WHERE t.serial_number  = m.msisdn
                                                AND t.cust_type = '1' AND trunc(t.start_date) = trunc(m.finish_time))
            THEN 1 ELSE null END) rsrv_str3 
FROM   ti_bi_mo_mon m 
WHERE  m.finish_time >= to_date(:START_TIME,'yyyy-mm-dd hh24:mi:ss') 
AND m.finish_time < to_date(:END_TIME,'yyyy-mm-dd hh24:mi:ss') 
GROUP BY trunc(m.finish_time)