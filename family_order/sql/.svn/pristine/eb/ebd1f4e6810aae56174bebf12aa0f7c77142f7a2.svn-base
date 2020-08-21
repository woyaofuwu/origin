SELECT a.deal_id,a.deal_tag,a.deal_state,a.deal_eparchy_code
  FROM (
         SELECT *
           FROM (
                  SELECT *
                    FROM (
                           SELECT /*+ index(a idx_batdeal_priority)*/
                                  a.operate_id deal_id,a.cancel_tag deal_tag,'2' deal_state,'' deal_eparchy_code,a.priority,a.exec_time
                             FROM tf_b_trade_batdeal a
                            WHERE a.deal_state = '1'
                              AND a.priority >= 0
                              AND a.exec_time <= SYSDATE
                          ORDER BY a.deal_state,a.priority,a.exec_time
                         )
                   WHERE ROWNUM <= :ROW_COUNT
                  UNION ALL
                  SELECT *
                    FROM (
                           SELECT /*+ INDEX(a idx_batdeal_priority)*/
                                  a.operate_id deal_id,a.cancel_tag deal_tag,'2' deal_state,'' deal_eparchy_code,a.priority,a.exec_time
                             FROM tf_b_trade_batdeal a
                            WHERE a.deal_state = 'A'
                              AND a.priority >= 0
                              AND a.exec_time <= SYSDATE
                          ORDER BY a.deal_state,a.priority,a.exec_time
                         )
                   WHERE ROWNUM <= :ROW_COUNT
                ) a
         ORDER BY a.priority,a.exec_time
       ) a
 WHERE ROWNUM <= :ROW_COUNT
$CONDITIONSQL