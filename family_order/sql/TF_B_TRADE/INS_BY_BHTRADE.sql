INSERT INTO tf_b_trade (
   trade_id,accept_month,batch_id,order_id,prod_order_id,bpm_id,campn_id,trade_type_code,priority,subscribe_type,in_mode_code,
   cust_id,cust_name,user_id,acct_id,serial_number,net_type_code,eparchy_code,city_code,product_id,brand_code,
   accept_date,trade_staff_id,trade_depart_id,trade_city_code,trade_eparchy_code,term_ip,
   oper_fee,foregift,advance_pay,invoice_no,fee_state,fee_time,fee_staff_id,process_tag_set,next_deal_tag,olcom_tag,
   exec_time,exec_action,exec_result,exec_desc,update_time,update_staff_id,update_depart_id,
   remark,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10,
   subscribe_state,
   intf_id,
   pf_wait,
   finish_date,
   cancel_date,
   cancel_tag,
   cancel_staff_id,
   cancel_depart_id,
   cancel_city_code,
   cancel_eparchy_code
)
SELECT trade_id,accept_month,batch_id,order_id,prod_order_id,bpm_id,campn_id,trade_type_code,priority,subscribe_type,in_mode_code,
       cust_id,cust_name,user_id,acct_id,serial_number,net_type_code,eparchy_code,city_code,product_id,brand_code,
       accept_date,trade_staff_id,trade_depart_id,trade_city_code,trade_eparchy_code,term_ip,
       oper_fee,foregift,advance_pay,invoice_no,fee_state,fee_time,fee_staff_id,process_tag_set,next_deal_tag,olcom_tag,
       exec_time,exec_action,exec_result,exec_desc,SYSDATE,:UPDATE_STAFF_ID,update_depart_id,
       remark,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_str4,rsrv_str5,rsrv_str6,rsrv_str7,rsrv_str8,rsrv_str9,rsrv_str10,
       subscribe_state,
	   intf_id,
	   pf_wait,
       NULL,
       cancel_date,
       CANCEL_TAG,
       CANCEL_STAFF_ID,
       CANCEL_DEPART_ID,
       CANCEL_CITY_CODE,
       CANCEL_EPARCHY_CODE
    FROM tf_bh_trade
   WHERE trade_id = TO_NUMBER(:TRADE_ID)
     AND cancel_tag = :CANCEL_TAG