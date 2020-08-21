SELECT accept_date,group_id,para_code1,feeitem_code,to_char(fee) fee,to_char(acct_id) acct_id,to_char(operate_time,'yyyy-mm-dd hh24:mi:ss') operate_time,operate_eparchy_code,operate_city_code,operate_depart_id,operate_staff_id 
FROM tm_a_group_bill
 WHERE accept_date=:ACCEPT_DATE
   AND trim(group_id)=:GROUP_ID
union all
SELECT accept_date,group_id,para_code1,feeitem_code,to_char(fee) fee,to_char(acct_id) acct_id,to_char(operate_time,'yyyy-mm-dd hh24:mi:ss') operate_time,operate_eparchy_code,operate_city_code,operate_depart_id,operate_staff_id 
FROM tm_product_order
 WHERE accept_date=:ACCEPT_DATE
   AND trim(group_id)=:GROUP_ID
union all
SELECT accept_date,group_id,para_code1,feeitem_code,to_char(fee) fee,to_char(acct_id) acct_id,to_char(operate_time,'yyyy-mm-dd hh24:mi:ss') operate_time,operate_eparchy_code,operate_city_code,operate_depart_id,operate_staff_id 
FROM ts_a_group_fee
 WHERE accept_date=:ACCEPT_DATE
   AND trim(group_id)=:GROUP_ID