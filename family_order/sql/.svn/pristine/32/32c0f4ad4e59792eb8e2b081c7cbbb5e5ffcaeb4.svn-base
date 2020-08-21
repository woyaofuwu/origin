--IS_CACHE=Y
SELECT para_code,para_name,rpay_deposit_code,deposit_code,gpay_deposit_code,gift_deposit_code,to_char(rpay_deposit) rpay_deposit,to_char(gpay_deposit) gpay_deposit,to_char(trans_fee) trans_fee,to_char(mgift_fee) mgift_fee,rsrv_str1,rsrv_str2,remark,update_eparchy_code,update_staff_id,update_depart_id,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,not_batchfee_tag,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date 
  FROM td_a_transmode
 WHERE para_code=:PARA_CODE