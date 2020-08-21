SELECT to_char(bill_id) bill_id,
       to_char(id) id,
       id_type,
       partition_id,
       effect_item_code,
       decode(act_mode,'0','按金额平摊','1','按金额平摊+帐目优先级','2','按用户优先级,金额平摊','3','按用户+帐目优先级') act_mode,
       decode(effect_mode,'0','优惠补费','1','优惠退费','2','优惠上限','3','优惠下限','4','优惠设定','5','帐前补费','6','帐前退费','7','帐前上限','8','帐前下限','9','帐前设定') effect_mode,
       decode(addup_mode,'0','固定作用','1','普通累计作用','2','特殊累计作用') ADDUP_MODE, 
       decode(effect_value_type,'0','金额','1','比例') EFFECT_VALUE_TYPE,
       to_char(effect_value) effect_value,
       to_char(refer_fee) refer_fee,
       start_acyc_id,
       end_acyc_id,
       to_char(remainfee) remainfee,
       to_char(use_fee1) use_fee1,
       to_char(use_fee2) use_fee2,
       to_char(limit_fee) limit_fee,
       recv_tag,
       adjust_reason_id,
       to_char(a.update_time, 'yyyy-mm-dd hh24:mi:ss') update_time,
       b.effect_cond_item adjust_depart_id,
       adjust_staff_id,
       adjust_eparchy_code
  FROM tf_a_adjustbbill a, td_a_effectconditem b
 WHERE id = TO_NUMBER(:ID)
   AND id_type = :ID_TYPE
   AND partition_id = :PARTITION_ID
   AND recv_tag = :RECV_TAG
   AND a.update_time >= TO_DATE(:START_DATE, 'YYYYMMDD')
   AND a.update_time <= add_months(TO_DATE(:END_DATE, 'YYYYMMDD'), 1)
   and a.effect_item_code = b.effect_cond_item_code(+)