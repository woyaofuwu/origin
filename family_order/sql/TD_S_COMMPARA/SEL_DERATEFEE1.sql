SELECT /*+ parallel(A,2)(B,2) full(A) */ a.serial_number para_code1,a.trade_id para_code2,(select trade_type from td_s_tradetype where trade_type_code = a.trade_type_code ) para_code3,to_char(b.oldfee/100,'99999990.99') para_code4,to_char((b.oldfee-b.fee)/100,'99999990.99') para_code5,to_char(a.finish_date,'yyyy-mm-dd hh24:mi:ss') para_code6,(select staff_id||'|'||staff_name from TD_M_STAFF where staff_id = a.trade_staff_id) 
 para_code7,(select depart_code||'|'||depart_name from TD_M_DEPART where depart_id = a.trade_depart_id) 
para_code8 ,0 param_attr,a.remark para_code9
 from tf_bh_trade a,tf_b_tradefee_sub b
where a.trade_id=b.trade_id
      AND :PARA_CODE4=a.trade_city_code 
      AND a.trade_type_code=to_number(:PARA_CODE1)
      AND a.trade_staff_id >= TRIM(:PARA_CODE5)
      AND a.trade_staff_id <= TRIM(:PARA_CODE6)
      AND a.accept_month = to_number(:ACCEPT_MONTH)
      AND b.accept_month = to_number(:ACCEPT_MONTH)
      AND a.cancel_tag='0'
      AND a.accept_date BETWEEN TO_DATE(:PARA_CODE2, 'yyyy-mm-dd hh24:mi:ss') AND TO_DATE(:PARA_CODE3,     'yyyy-mm-dd hh24:mi:ss')
      AND b.oldfee>b.fee 