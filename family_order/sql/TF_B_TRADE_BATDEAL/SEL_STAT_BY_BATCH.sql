SELECT a.cancel_tag||'-'||DECODE(a.cancel_tag,'0','正常','1','返销','未知') cancel_tag,
       a.deal_state||'-'||b.data_name deal_state,
       COUNT(*) trade_num 
  FROM tf_b_trade_batdeal a,td_s_static b
 WHERE a.batch_id = TO_NUMBER(:BATCH_ID)
   AND a.deal_state = b.data_id(+)
   AND b.type_id(+) = 'BAT_DEAL_STATE'
GROUP BY a.cancel_tag,a.deal_state,b.data_name  
ORDER BY a.cancel_tag,a.deal_state