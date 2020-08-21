SELECT accept_month,to_char(order_id) order_id,trade_type_code,to_char(prod_order_id) prod_order_id,brand_code,product_id,product_mode,comp_tag,to_char(oper_fee) oper_fee,to_char(foregift) foregift,to_char(advance_pay) advance_pay,fee_state,fee_staff_id,to_char(fee_time,'yyyy-mm-dd hh24:mi:ss') fee_time 
  FROM tf_b_order_product
 WHERE accept_month=:ACCEPT_MONTH
   AND order_id=TO_NUMBER(:ORDER_ID)