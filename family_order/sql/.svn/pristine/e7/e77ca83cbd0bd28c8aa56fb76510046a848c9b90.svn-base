select t.return_type,t.order_id,t.sub_order_id,t.return_id,t.channel_id,t.ext_return_id,t.buyer_return_time,t.update_time,t.status,t.update_staff_id,t.update_depart_id,t.opr_num,t.accept_date 
from TF_B_CTRM_RETURN t 
where 1 = 1
and (t.RSRV_STR2 = '0000' or t.RSRV_STR2 is null)
and t.order_id = :ORDER_ID
and t.sub_order_id = :SUB_ORDER_ID 
and t.return_id = :RETURN_ID
