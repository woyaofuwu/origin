SELECT to_char(trade_id) trade_id,to_char(batch_id) batch_id,to_char(bpm_id) bpm_id,trade_type_code,
       f_csm_getnamebycode(trade_type_code,:EPARCHY_CODE,'TD_S_TRADETYPE') TRADE_TYPE,
       in_mode_code,
       priority,
       subscribe_state,
       next_deal_tag,
       product_id,
       f_csm_getnamebycode(product_id, :EPARCHY_CODE, 'TD_B_PRODUCT') PRODUCT_NAME,
       brand_code,
       f_csm_getnamebycode(brand_code, :EPARCHY_CODE, 'TD_S_BRAND') brand,
to_char(user_id) user_id,to_char(cust_id) cust_id,to_char(acct_id) acct_id,
serial_number,cust_name,to_char(accept_date,'yyyy-mm-dd hh24:mi:ss') accept_date,accept_month,
trade_staff_id,trade_depart_id,
trade_city_code,trade_eparchy_code,term_ip,eparchy_code,city_code,olcom_tag,
to_char(exec_time,'yyyy-mm-dd hh24:mi:ss') exec_time,
to_char(finish_date,'yyyy-mm-dd hh24:mi:ss') finish_date,to_char(oper_fee) oper_fee,
to_char(foregift) foregift,to_char(advance_pay) advance_pay,invoice_no,fee_state,
to_char(fee_time,'yyyy-mm-dd hh24:mi:ss') fee_time,fee_staff_id,
cancel_tag,to_char(cancel_date,'yyyy-mm-dd hh24:mi:ss') cancel_date,
cancel_staff_id,cancel_depart_id,cancel_city_code,cancel_eparchy_code,process_tag_set,rsrv_str1,rsrv_str2,rsrv_str3,
rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10,remark 
  FROM tf_bh_trade
  WHERE (trade_type_code=:TRADE_TYPE_CODE OR :TRADE_TYPE_CODE='-1')
  AND user_id=TO_NUMBER(:USER_ID)
  AND (to_char(accept_date,'YYYY-MM-DD') >=:START_DATE OR :START_DATE IS NULL)
   	and (to_char(accept_date,'YYYY-MM-DD') < :FINISH_DATE OR :FINISH_DATE IS NULL)
  AND NOT EXISTS (SELECT 1 FROM td_s_commpara b WHERE b.subsys_code='CSM'
  AND b.param_attr=7800 AND trade_staff_id = b.para_code1 AND trade_type_code=TO_NUMBER(b.para_code2)
  AND b.end_date>SYSDATE)
UNION ALL
SELECT to_char(trade_id) trade_id,to_char(batch_id) batch_id,to_char(bpm_id) bpm_id,trade_type_code,
       f_csm_getnamebycode(trade_type_code,:EPARCHY_CODE,'TD_S_TRADETYPE') TRADE_TYPE,
       in_mode_code,
       priority,
       subscribe_state,
       next_deal_tag,
       product_id,
       f_csm_getnamebycode(product_id, :EPARCHY_CODE, 'TD_B_PRODUCT') PRODUCT_NAME,
       brand_code,
       f_csm_getnamebycode(brand_code, :EPARCHY_CODE, 'TD_S_BRAND') brand,
to_char(user_id) user_id,to_char(cust_id) cust_id,to_char(acct_id) acct_id,
serial_number,cust_name,to_char(accept_date,'yyyy-mm-dd hh24:mi:ss') accept_date,accept_month,
trade_staff_id,trade_depart_id,
trade_city_code,trade_eparchy_code,term_ip,eparchy_code,city_code,olcom_tag,
to_char(exec_time,'yyyy-mm-dd hh24:mi:ss') exec_time,
to_char(finish_date,'yyyy-mm-dd hh24:mi:ss') finish_date,to_char(oper_fee) oper_fee,
to_char(foregift) foregift,to_char(advance_pay) advance_pay,invoice_no,fee_state,
to_char(fee_time,'yyyy-mm-dd hh24:mi:ss') fee_time,fee_staff_id,
cancel_tag,to_char(cancel_date,'yyyy-mm-dd hh24:mi:ss') cancel_date,
cancel_staff_id,cancel_depart_id,cancel_city_code,cancel_eparchy_code,process_tag_set,rsrv_str1,rsrv_str2,rsrv_str3,
rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10,remark 
  FROM tf_bh_trade
  WHERE (trade_type_code=:TRADE_TYPE_CODE OR :TRADE_TYPE_CODE='-1')
  AND trade_id IN (
  	SELECT trade_id FROM tf_b_trade_relation WHERE USER_ID_B=TO_NUMBER(:USER_ID))
  AND (to_char(accept_date,'YYYY-MM-DD') >=:START_DATE OR :START_DATE IS NULL)
   	and (to_char(accept_date,'YYYY-MM-DD') < :FINISH_DATE OR :FINISH_DATE IS NULL)
  AND NOT EXISTS (SELECT 1 FROM td_s_commpara b WHERE b.subsys_code='CSM'
  AND b.param_attr=7800 AND trade_staff_id = b.para_code1 AND trade_type_code=TO_NUMBER(b.para_code2)
  AND b.end_date>SYSDATE)
order by accept_date desc