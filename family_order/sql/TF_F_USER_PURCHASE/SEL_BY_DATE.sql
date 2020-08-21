SELECT to_char(a.user_id) user_id,a.serial_number,purchase_attr,purchase_desc,purchase_mode,
purchase_info,to_char(a.trade_id) trade_id,to_char(rpay_mpfee) rpay_mpfee,feeitem_code,
to_char(rpay_foregift) rpay_foregift,foregift_code,to_char(rpay_deposit) rpay_deposit,rpay_deposit_code,
to_char(left_deposit) left_deposit,to_char(mrtn_fee) mrtn_fee,rtn_type,rtn_months,
left_months,to_char(gpay_deposit) gpay_deposit,gpay_deposit_code,to_char(left_gdeposit) left_gdeposit,
to_char(mgift_fee) mgift_fee,gift_type,gtotal_months,gleft_months,to_char(month_fee) month_fee,ntotal_months,
nleft_months,device_type,imei,assure_no,discnt_code,a.rsrv_str1,a.rsrv_str2,a.rsrv_str3,a.rsrv_str4,a.rsrv_str5,
a.rsrv_str6,a.rsrv_str7,a.rsrv_str8,a.rsrv_str9,a.rsrv_str10,process_tag,staff_id,depart_id,
to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,
to_char(a.finish_date,'yyyy-mm-dd hh24:mi:ss') finish_date,a.remark 
from  tf_f_user_purchase a,tf_bh_trade b 
where a.trade_id=b.Trade_id
and b.trade_type_code=240
and b.trade_eparchy_code=:EPARCHY_CODE
and b.accept_date>=TO_DATE(:START_DATE||' 00:00:00','yyyy-mm-dd hh24:mi:ss')
and b.accept_date<=TO_DATE(:END_DATE||' 23:59:59','yyyy-mm-dd hh24:mi:ss')