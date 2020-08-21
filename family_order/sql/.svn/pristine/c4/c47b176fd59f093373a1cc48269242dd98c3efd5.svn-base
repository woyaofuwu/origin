SELECT to_char(trade_id) trade_id,contract_no,cust_name,feeitem_code,to_char(fee) fee,group_id,pay_mode_code,pay_tag,contact,contact_phone,prevalue1,prevalue2,prevalue3,prevalue4,prevalue5,to_char(prevaluen1) prevaluen1,to_char(prevaluen2) prevaluen2,to_char(prevaluen3) prevaluen3,to_char(prevalued1,'yyyy-mm-dd hh24:mi:ss') prevalued1,to_char(prevalued2,'yyyy-mm-dd hh24:mi:ss') prevalued2,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,remark,to_char(trade_time,'yyyy-mm-dd hh24:mi:ss') trade_time,trade_staff_id,trade_depart_id,eparchy_code,city_code 
  FROM tf_f_othertradefee
 WHERE trade_time BETWEEN TO_DATE(:PREVALUE1, 'YYYY-MM-DD HH24:MI:SS') AND TO_DATE(:PREVALUE2, 'YYYY-MM-DD HH24:MI:SS')
   AND ((start_date BETWEEN TO_DATE(:PREVALUE3, 'YYYY-MM-DD HH24:MI:SS') AND TO_DATE(:PREVALUE4, 'YYYY-MM-DD HH24:MI:SS')) or :PREVALUE3 IS NULL)
   AND ((end_date BETWEEN TO_DATE(:PREVALUE5, 'YYYY-MM-DD HH24:MI:SS') AND TO_DATE(:PREVALUE6, 'YYYY-MM-DD HH24:MI:SS')) OR :PREVALUE5 IS NULL)
   AND (pay_tag = TO_NUMBER(:PAY_TAG) OR TO_NUMBER(:PAY_TAG) = -1)
   AND (group_id = :GROUP_ID OR :GROUP_ID = '-1')
   AND trade_depart_id=:TRADE_DEPART_ID