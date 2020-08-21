select 
we.sub_ibsysid,
we.ibsysid,
we.product_id,
we.sheettype, 
wed.busi_state, 
wed.product_no,
wed.trade_id,
decode(we.deal_state,'0','未处理','1','已发送','2','处理成功','处理失败') deal_state,
decode(we.oper_type,'newWorkSheet','派单','renewWorkSheet','重派单','replyWorkSheet','回复','withdrawWorkSheet','驳回','untreadWorkSheet','退回','confirmWorkSheet','受理','checkinWorkSheet','归档','suggestWorkSheet','阶段通知','notifyWorkSheet','阶段回复','replyError','程序性错误',we.oper_type) oper_type,
decode(we.product_id,'5080','集团专线-裸光纤','5081','集团专线-数字电路','5083','集团专线-互联网接入',we.product_id) product_name,
nvl2(we.eoms_order_code,'完工','在途') order_type,WE.RECORD_NUM
from TF_B_EOP_EOMS we,TF_B_EOP_EOMS_DETAIL wed
where we.ibsysid=wed.ibsysid 
and we.RECORD_NUM=wed.RECORD_NUM
and (we.RECORD_NUM,we.insert_time) in (
     select wed1.RECORD_NUM,max(we1.insert_time) max_insert_time
     from TF_B_EOP_EOMS we1,TF_B_EOP_EOMS_DETAIL wed1
     where we1.ibsysid=wed1.ibsysid and we1.RECORD_NUM=wed1.RECORD_NUM
     group by wed1.RECORD_NUM
)
and we.ibsysid=:IBSYSID