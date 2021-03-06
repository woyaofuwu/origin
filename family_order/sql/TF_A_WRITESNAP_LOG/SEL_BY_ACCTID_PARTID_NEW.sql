SELECT to_char(operate_id) operate_id,operate_type,partition_id,to_char(acct_id) acct_id,writeoff_mode,to_char(spay_fee) spay_fee,to_char(all_money) all_money,to_char(all_new_money) all_new_money,to_char(all_balance) all_balance,to_char(all_new_balance) all_new_balance,to_char(allbowe_fee) allbowe_fee,to_char(allrowe_fee) allrowe_fee,recover_tag,to_char(old_round_fee) old_round_fee,to_char(new_round_fee) new_round_fee,to_char(operate_time,'yyyy-mm-dd hh24:mi:ss') operate_time,cancel_tag,to_char(allnewbowe_fee) allnewbowe_fee,to_char(prereal_fee) prereal_fee,to_char(curreal_fee) curreal_fee,rsrv_tag1,rsrv_info1,rsrv_info2,remark,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_fee1) rsrv_fee1,to_char(rsrv_fee2) rsrv_fee2,eparchy_code,to_char(rsrv_fee3) rsrv_fee3
FROM TF_A_WRITESNAP_LOG 
WHERE acct_id = :ACCT_ID    AND partition_id>=:START_PARTITION_ID
   AND partition_id<=:END_PARTITION_ID
   AND operate_time<=TO_DATE(:END_RECV_TIME, 'YYYY-MM-DD HH24:MI:SS')
   AND operate_time>=TO_DATE(:START_RECV_TIME, 'YYYY-MM-DD HH24:MI:SS')
 AND cancel_tag = '0'  
UNION ALL
SELECT to_char(operate_id) operate_id,operate_type,partition_id,to_char(acct_id) acct_id,writeoff_mode,to_char(spay_fee) spay_fee,to_char(all_money) all_money,to_char(all_new_money) all_new_money,to_char(all_balance) all_balance,to_char(all_new_balance) all_new_balance,to_char(allbowe_fee) allbowe_fee,to_char(allrowe_fee) allrowe_fee,recover_tag,to_char(old_round_fee) old_round_fee,to_char(new_round_fee) new_round_fee,to_char(operate_time,'yyyy-mm-dd hh24:mi:ss') operate_time,cancel_tag,to_char(allnewbowe_fee) allnewbowe_fee,to_char(prereal_fee) prereal_fee,to_char(curreal_fee) curreal_fee,rsrv_tag1,rsrv_info1,rsrv_info2,remark,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_fee1) rsrv_fee1,to_char(rsrv_fee2) rsrv_fee2,eparchy_code,to_char(rsrv_fee3) rsrv_fee3
FROM TF_A_WRITESNAP_LOG_D 
WHERE acct_id = :ACCT_ID AND  partition_id>=:START_PARTITION_ID
   AND partition_id<=:END_PARTITION_ID
   AND operate_time<=TO_DATE(:END_RECV_TIME, 'YYYY-MM-DD HH24:MI:SS')
   AND operate_time>=TO_DATE(:START_RECV_TIME, 'YYYY-MM-DD HH24:MI:SS')
 AND cancel_tag = '0'  
ORDER BY operate_time ASC