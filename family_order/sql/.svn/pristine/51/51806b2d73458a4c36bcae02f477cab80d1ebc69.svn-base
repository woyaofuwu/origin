--IS_CACHE=Y
SELECT a.batch_oper_type,
       a.batch_oper_name,
       a.class_code,
       NVL(a.right_code,'BAT_'||a.batch_oper_type) right_code,
       a.tag_set,
       a.comp_popaction,
       a.priority,
       NVL(a.trade_type_code,0) trade_type_code,
       a.use_tag,
       a.trade_attr,
       a.deal_type,
       a.need_activenow_tag,
       a.cancelable_flag,
           a.repeat_tag,
       a.condition_tag,
           a.limit_num_batch,
           a.limit_num_day,
           a.limit_num_mon
  FROM (--取本地市的配置
        SELECT a.*,b.limit_num_batch,b.limit_num_day,b.limit_num_mon
          FROM td_b_batchtype a,td_b_batchclass b
         WHERE a.use_tag = '1'
           AND (a.batch_oper_type = :BATCH_OPER_TYPE OR :BATCH_OPER_TYPE IS NULL)
           AND b.class_code = a.class_code
           AND b.eparchy_code = :EPARCHY_CODE
        UNION ALL
        --没有本地市配置的取全省的配置　
        SELECT a.*,b.limit_num_batch,b.limit_num_day,b.limit_num_mon
          FROM td_b_batchtype a,td_b_batchclass b
         WHERE a.use_tag = '1'
           AND (a.batch_oper_type = :BATCH_OPER_TYPE OR :BATCH_OPER_TYPE IS NULL)
           AND b.class_code = a.class_code
           AND b.eparchy_code = 'ZZZZ'
           AND NOT EXISTS (SELECT 1 FROM td_b_batchclass c WHERE c.class_code = a.class_code AND c.eparchy_code = :EPARCHY_CODE)
        UNION ALL
        --两者都没有的取默认配置　
        SELECT a.*,0 limit_num_batch,0 limit_num_day,0 limit_num_mon
          FROM td_b_batchtype a
         WHERE a.use_tag = '1'
           AND (a.batch_oper_type = :BATCH_OPER_TYPE OR :BATCH_OPER_TYPE IS NULL)
           AND NOT EXISTS (SELECT 1  FROM td_b_batchclass c 
                            WHERE c.class_code = a.class_code 
                              AND (c.eparchy_code = :EPARCHY_CODE OR c.eparchy_code = 'ZZZZ')
                          )
       ) a
