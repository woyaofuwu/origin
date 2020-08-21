SELECT a.record_no deal_id,'' deal_tag,a.update_state old_state,'1' deal_state,'' deal_eparchy_code
  FROM (
         SELECT *
           FROM (
                  SELECT '0' order_tag,ROWNUM order_no,a.* 
                    FROM (
                            SELECT record_no,update_state
                              FROM ti_b_batchtrade_state
                             WHERE record_time < SYSDATE
                               AND update_state IN ('0')
                               AND NVL(TO_NUMBER(SUBSTR(trade_id,-1,1)),0) >= 5
                            ORDER BY record_time
                         ) a
                   WHERE ROWNUM <= :ROW_COUNT
                  UNION ALL
                  SELECT '1' order_tag,ROWNUM order_no,a.* 
                    FROM (
                            SELECT record_no,update_state
                              FROM ti_b_batchtrade_state
                             WHERE record_time < SYSDATE
                               AND update_state IN ('3')
                               AND NVL(TO_NUMBER(SUBSTR(trade_id,-1,1)),0) >= 5
                            ORDER BY record_time
                         ) a
                   WHERE ROWNUM <= :ROW_COUNT
               ) a
         ORDER BY order_tag,order_no
       ) a
 WHERE ROWNUM <= :ROW_COUNT
   $CONDITIONSQL