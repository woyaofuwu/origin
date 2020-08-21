SELECT a.partition_id,to_char(a.user_id) user_id,to_char(a.acct_id) acct_id,a.payitem_code,a.acct_priority,a.user_priority,a.bind_type,a.start_cycle_id,a.end_cycle_id,a.default_tag,a.act_tag,a.limit_type,to_char(a.limit) limit,a.complement_tag,a.update_staff_id,a.update_depart_id,to_char(a.update_time,'yyyy-mm-dd hh24:mi:ss') update_time , b.item_name as pay_item,b.*
  FROM tf_a_payrelation a, td_b_item b
  WHERE a.payitem_code = b.item_id(+)
  AND TO_CHAR(SYSDATE,'YYYYMMDD')BETWEEN b.start_cycle_id(+) and b.end_cycle_id(+)
  AND b.ITEM_USE_TYPE(+)='1'
  AND a.default_tag = '0'
  AND a.act_tag = '1'
  AND TO_CHAR(SYSDATE,'YYYYMMDD')BETWEEN a.start_cycle_id and a.end_cycle_id
  AND a.user_id=TO_NUMBER(:USER_ID) and a.partition_id=mod(TO_NUMBER(:USER_ID),10000)