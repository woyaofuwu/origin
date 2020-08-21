select t1.order_id,t2.suborder_id,t1.channel_code,t1.extorder_id,t2.create_time,t2.update_time,t2.serviceno,t2.number_oprtype,t2.serviceno_type,t2.state,t2.update_staff_id,t2.update_depart_id,t1.country,t1.province,t1.city,t1.district,t1.address,t1.post_code,t1.mobile_phone,t1.land_line,t2.goods_title,t2.rsrv_str3,t1.rsrv_str10 
from TF_B_CTRM_GERLORDER t1,TF_B_CTRM_GERLSUBORDER t2 
where 1= 1
and (t1.order_id = t2.order_id) 
and t2.number_oprtype IN ('03','04','05')
and (t2.DEAL_STATE = '0000' or t2.DEAL_STATE is null)
and t1.order_id = :ORDER_ID 
and t2.suborder_id = :SUB_ORDER_ID
