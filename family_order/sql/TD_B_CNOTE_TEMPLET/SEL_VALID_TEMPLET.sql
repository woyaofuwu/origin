--IS_CACHE=Y
SELECT tpttype.templet_name,
       tpttype.templet_type,
       tpt.templet_code,
       tptrela.relation_kind,
       tptrela.relation_attr,
       tptrela.trade_type_code,
       tptrela.priority
     FROM TD_B_CNOTE_TEMPLET tpt,
     TD_B_CNOTE_TEMPLET_RELA tptrela,
     td_b_cnote_templet_type tpttype
     WHERE tpt.templet_code = tptrela.TEMPLET_CODE 
     and tpt.templet_type = tpttype.templet_type
     and tpt.use_tag='0'
     and tptrela.relation_kind=:RELATION_KIND
     and (tptrela.relation_attr=:RELATION_ATTR or tptrela.relation_attr='ZZZZ')
     and (tptrela.trade_type_code=:TRADE_TYPE_CODE or tptrela.trade_type_code='-1')
     and tpttype.templet_type=:TEMPLET_TYPE
     order by tptrela.trade_type_code desc,tptrela.relation_attr,tptrela.priority desc