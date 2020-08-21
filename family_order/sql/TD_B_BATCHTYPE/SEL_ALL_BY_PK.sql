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
       a.ss_service,
       a.trans_class
  FROM td_b_batchtype a
 WHERE a.use_tag = '1'
   AND a.batch_oper_type = :BATCH_OPER_TYPE
