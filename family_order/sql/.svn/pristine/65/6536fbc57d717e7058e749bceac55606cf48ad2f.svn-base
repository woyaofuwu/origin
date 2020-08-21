SELECT accept_date,group_id,para_code1,decode(substr(feeitem_code,1,1),'0',substr(feeitem_code,2,1),feeitem_code)feeitem_code ,to_char(fee) fee,to_char(acct_id) acct_id ,to_char(operate_time,'yyyy-mm-dd hh24:mi:ss') operate_time,operate_eparchy_code,operate_city_code,operate_depart_id,operate_staff_id 

FROM ts_a_group_fee
 WHERE accept_date=:ACCEPT_DATE
   AND trim(group_id)=:GROUP_ID
 order by para_code1,feeitem_code