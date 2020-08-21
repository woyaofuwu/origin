INSERT INTO tf_f_user_specialepay(partition_id,user_id,user_id_a,acct_id,acct_id_b,payitem_code,start_acyc_id,end_acyc_id,bind_type,limit_type,limit,complement_tag,rsrv_str1,rsrv_str2,rsrv_str3,update_staff_id,update_depart_id,update_time)
SELECT mod(user_id,10000),user_id,to_number(:USER_ID_A),acct_id,to_number(:ACCT_ID_B),payitem_code,
  start_acyc_id,end_acyc_id,bind_type,limit_type,limit,complement_tag,:RSRV_STR1,null,
  null,:TRADE_STAFFID,:TRADE_DEPART_ID,sysdate
FROM tf_b_trade_payrelation
WHERE trade_id=to_number(:TRADE_ID)