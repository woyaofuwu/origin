SELECT to_char(user_id) user_id,to_char(acct_id) acct_id,to_char(fee) fee,acyc_id,reason,remark,to_char(rsrv_fee1) rsrv_fee1,to_char(rsrv_date,'yyyy-mm-dd hh24:mi:ss') rsrv_date,rsrv_info,to_char(rsrv_info1) rsrv_info1,rsrv_info2 
  FROM tf_a_specialremark
 WHERE acct_id=TO_NUMBER(:ACCT_ID)
   AND acyc_id=:ACYC_ID
   AND reason=:REASON