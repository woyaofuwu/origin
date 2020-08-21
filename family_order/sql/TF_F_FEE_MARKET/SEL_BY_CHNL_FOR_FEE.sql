SELECT chnl_id,factory_code,balance/100 balance,year,to_char(accept_month) accept_month,to_char(start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(end_date,'yyyy-mm-dd hh24:mi:ss') end_date,rsrv_num1,rsrv_num2,rsrv_num3,decode(status,'0','未缴清','1','已缴清','未知') rsrv_str1,rsrv_str2,rsrv_str3,update_staff,to_char(update_time,'yyyy-mm-dd hh24:mi:ss') update_time,status,remark 
  FROM tf_f_fee_market
 WHERE chnl_id=:CHNL_ID
   AND factory_code=:FACTORY_CODE
   AND status=:STATUS