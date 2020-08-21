SELECT to_char(a.user_id) user_id,a.serial_number, a.purchase_attr, a.purchase_desc,
       a.purchase_mode, a.purchase_info,to_char(a.trade_id) trade_id,to_char(a.rpay_mpfee/100) rpay_mpfee,
       a.feeitem_code,to_char(a.rpay_foregift/100) rpay_foregift,a.foregift_code,to_char(a.deposit/100) rpay_deposit,
       a.rpay_deposit_code,to_char(a.left_deposit/100) left_deposit,to_char(a.mrtn_fee/100) mrtn_fee,a.rtn_type,
       a.rtn_months,a.left_months,to_char(a.gpay_deposit/100) gpay_deposit,a.gpay_deposit_code,
       to_char(a.left_gdeposit/100) left_gdeposit,to_char(a.mgift_fee/100) mgift_fee,a.gift_type,
       a.gtotal_months,a.gleft_months,to_char(a.month_fee/100) month_fee,a.ntotal_months,
       a.nleft_months,a.device_type,a.imei,a.assure_no,a.discnt_code,a.rsrv_str1,a.rsrv_str2,a.rsrv_str3,
       a.rsrv_str4,a.rsrv_str5,a.rsrv_str6,a.rsrv_str7,a.rsrv_str8,a.rsrv_str9,a.rsrv_str10,
       a.process_tag,a.staff_id,a.depart_id,to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,
       to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date,to_char(a.finish_date,'yyyy-mm-dd hh24:mi:ss') finish_date,
       a.remark,c.param_name
  FROM tf_f_user_purchase a,
       tf_bh_trade b,
       td_s_commpara c
 WHERE a.trade_id = b.trade_id
   AND b.rsrv_str4 = c.para_code1   
   AND c.param_code = a.purchase_attr
   AND c.para_code3 = a.purchase_mode
   AND (c.eparchy_code = 'ZZZZ' OR c.eparchy_code = b.eparchy_code)
   AND c.param_attr = 3007
   AND a.serial_number = :SERIAL_NUMBER
   AND (a.process_tag = :PROCESS_TAG OR :PROCESS_TAG = '9') --PROCESS_TAG 处理标志：0-正常  1-结束  2-取消 3-返销 9-所有
   AND (SYSDATE < a.end_date AND :PROCESS_TAG = '0' OR :PROCESS_TAG != '0')
   AND a.user_id IN
       (SELECT user_id
          FROM tf_f_user
         WHERE serial_number = :SERIAL_NUMBER
           AND (((remove_tag = '0' OR remove_tag = '1' OR remove_tag = '3') AND
               :REMOVE_TAG != '9') --REMOVE_TAG 0-正常0 1 3,9-非正常 其它状态
               OR ((remove_tag != '0' AND remove_tag != '1' AND
               remove_tag != '3') AND :REMOVE_TAG = '9')))