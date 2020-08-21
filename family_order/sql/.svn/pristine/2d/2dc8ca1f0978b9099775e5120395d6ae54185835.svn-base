SELECT to_char(a.user_id) user_id,a.serial_number,a.purchase_attr,a.purchase_desc,a.purchase_mode,a.purchase_info,to_char(a.trade_id) trade_id,to_char(a.rpay_mpfee) rpay_mpfee,a.feeitem_code,to_char(a.rpay_foregift) rpay_foregift,a.foregift_code,to_char(a.rpay_deposit) rpay_deposit,a.rpay_deposit_code,to_char(a.left_deposit) left_deposit,to_char(a.mrtn_fee) mrtn_fee,a.rtn_type,a.rtn_months,a.left_months,to_char(a.gpay_deposit) gpay_deposit,a.gpay_deposit_code,to_char(a.left_gdeposit) left_gdeposit,to_char(a.mgift_fee) mgift_fee,a.gift_type,a.gtotal_months,a.gleft_months,to_char(a.month_fee) month_fee,a.ntotal_months,a.nleft_months,a.device_type,a.imei,a.assure_no,a.discnt_code,a.rsrv_str1,a.rsrv_str2,a.rsrv_str3,a.rsrv_str4,a.rsrv_str5,a.rsrv_str6,a.rsrv_str7,a.rsrv_str8,a.rsrv_str9,a.rsrv_str10,a.process_tag,a.staff_id,a.depart_id,to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,to_char(a.finish_date,'yyyy-mm-dd hh24:mi:ss') finish_date,a.remark 
  FROM tf_f_user_purchase a,tf_f_relation_uu b
 WHERE b.user_id_a=TO_NUMBER(:USER_ID)
   AND a.user_id=b.user_id_b
   AND b.relation_type_code=:RELATION_TYPE_CODE
   AND a.process_tag=:PROCESS_TAG
   AND exists (select 1 from td_s_commpara b 
                where b.param_attr=9987
                  AND b.subsys_code='CSM'
                  AND sysdate between b.start_date and b.end_date
                  AND b.eparchy_code=:EPARCHY_CODE
                  AND b.param_code=a.purchase_mode)
   AND b.end_date >= sysdate
   AND a.rpay_deposit = 0