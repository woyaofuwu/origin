SELECT a.trade_id deal_id,a.cancel_tag deal_tag,a.subscribe_state old_state,'1' deal_state,a.eparchy_code deal_eparchy_code
  FROM (
         SELECT a.*
           FROM (
                   SELECT '0' order_tag,ROWNUM order_no,a.*             --优先处理0状态
                     FROM (
                             SELECT trade_id,cancel_tag,NVL(eparchy_code,trade_eparchy_code) eparchy_code,serial_number,
                                    subscribe_type,subscribe_state,trade_type_code,priority,exec_time
                               FROM tf_b_trade
                              WHERE subscribe_type BETWEEN 200 AND 299
                                AND subscribe_state IN ('0')
                                AND exec_time < SYSDATE
                                AND NVL(SUBSTR(serial_number,-1,1),'0') >= '5'
                             ORDER BY priority DESC,exec_time
                          ) a
                    WHERE ROWNUM <= :ROW_COUNT
                   UNION ALL
                   SELECT '1' order_tag,ROWNUM order_no,a.*             --0状态处理完成后再处理G状态
                     FROM (
                             SELECT trade_id,cancel_tag,NVL(eparchy_code,trade_eparchy_code) eparchy_code,serial_number,
                                    subscribe_type,subscribe_state,trade_type_code,priority,exec_time
                               FROM tf_b_trade
                              WHERE subscribe_type BETWEEN 200 AND 299
                                AND subscribe_state IN ('G','8')
                                AND exec_time < SYSDATE
                                AND NVL(SUBSTR(serial_number,-1,1),'0') >= '5'
                             ORDER BY priority DESC,exec_time
                          ) a
                    WHERE ROWNUM <= :ROW_COUNT
                ) a
         ORDER BY order_tag,order_no
       ) a
 WHERE ROWNUM <= :ROW_COUNT
   $CONDITIONSQL