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
       a.condition_tag
  FROM td_b_batchtype a
 WHERE a.use_tag = '1'
   AND a.trade_attr = :TRADE_ATTR
   AND not exists (select 1 from td_s_commpara c where c.param_attr = '2109' and c.subsys_code = 'CSM' and c.eparchy_code = 'ZZZZ' and sysdate between c.start_date and c.end_date
   AND (to_char(sysdate,'dd') between c.para_code1 and c.para_code2 or to_char(sysdate,'dd') between c.para_code3 and c.para_code4) and c.param_code=a.batch_oper_type)