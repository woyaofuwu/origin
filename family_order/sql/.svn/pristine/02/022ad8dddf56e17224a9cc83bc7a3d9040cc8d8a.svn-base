INSERT INTO tf_a_writesnap_log_rc(operate_id,operate_type,partition_id,acct_id,writeoff_mode,spay_fee,all_money,all_new_money,all_balance,all_new_balance,allbowe_fee,allrowe_fee,recover_tag,old_round_fee,new_round_fee,operate_time,cancel_tag,allnewbowe_fee,prereal_fee,curreal_fee,rsrv_tag1,rsrv_info1,rsrv_info2,remark,rsrv_date1,rsrv_date2,rsrv_fee1,rsrv_fee2,eparchy_code,rsrv_fee3)
SELECT operate_id,operate_type,partition_id,acct_id,writeoff_mode,spay_fee,all_money,all_new_money,all_balance,all_new_balance,allbowe_fee,allrowe_fee,recover_tag,old_round_fee,new_round_fee,operate_time,cancel_tag,allnewbowe_fee,prereal_fee,curreal_fee,rsrv_tag1,rsrv_info1,rsrv_info2,remark,rsrv_date1,rsrv_date2,rsrv_fee1,rsrv_fee2,eparchy_code,rsrv_fee3
FROM tf_a_writesnap_log
WHERE operate_id=TO_NUMBER(:OPERATE_ID)
   AND operate_type=:OPERATE_TYPE
   AND partition_id >= :START_PARTITION_ID
   AND partition_id <= :END_PARTITION_ID
   AND cancel_tag = '0'