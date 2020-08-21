SELECT group_id,order_oper,command,apply_phone,apply_name,apply_add,to_char(apply_time,'yyyy-mm-dd hh24:mi:ss') apply_time,to_char(apply_finish_time,'yyyy-mm-dd hh24:mi:ss') apply_finish_time,coper_busi,instruction,mark,deal_tag,deal_staff_id,to_char(deal_time,'yyyy-mm-dd hh24:mi:ss') deal_time,deal_desc 
  FROM tf_b_group_booking
 WHERE (order_oper=:ORDER_OPER OR :ORDER_OPER IS NULL)
   AND (command=:COMMAND   OR   :COMMAND IS NULL)
   AND ( ( apply_time>=TO_DATE(:START_DATE, 'YYYY-MM-DD HH24:MI:SS') AND apply_time<=TO_DATE(:END_DATE, 'YYYY-MM-DD HH24:MI:SS'))
        OR (:START_DATE IS NULL  AND  :END_DATE IS NULL ) )
   AND ( deal_tag=:DEAL_TAG OR :DEAL_TAG IS NULL )