SELECT /*+ index(a IDX_TF_AHB_SNAP_LOG_2008_ACCT) */to_char(operate_id) operate_id,operate_type,partition_id,to_char(acct_id) acct_id,writeoff_mode,to_char(spay_fee) spay_fee,to_char(all_money) all_money,to_char(all_new_money) all_new_money,to_char(all_balance) all_balance,to_char(all_new_balance) all_new_balance,to_char(allbowe_fee) allbowe_fee,to_char(allrowe_fee) allrowe_fee,recover_tag,to_char(old_round_fee) old_round_fee,to_char(new_round_fee) new_round_fee,to_char(operate_time,'yyyy-mm-dd hh24:mi:ss') operate_time,cancel_tag,to_char(allnewbowe_fee) allnewbowe_fee,to_char(prereal_fee) prereal_fee,to_char(curreal_fee) curreal_fee,rsrv_tag1,rsrv_info1,rsrv_info2,remark,to_char(rsrv_date1,'yyyy-mm-dd hh24:mi:ss') rsrv_date1,to_char(rsrv_date2,'yyyy-mm-dd hh24:mi:ss') rsrv_date2,to_char(rsrv_fee1) rsrv_fee1,to_char(rsrv_fee2) rsrv_fee2,eparchy_code,to_char(rsrv_fee3) rsrv_fee3 
  FROM tf_ahb_writesnap_log_2008 a
 WHERE partition_id >= :PARTITION_ID1 and partition_id <= :PARTITION_ID2 AND
       eparchy_code = :EPARCHY_CODE AND acct_id IN (SELECT ACCT_ID FROM TF_A_PAYRELATION WHERE
USER_ID = TO_NUMBER(:USER_ID) AND PARTITION_ID = MOD(TO_NUMBER(:USER_ID),10000))  AND
       operate_time+0 <= TO_DATE(:RECV_TIME_E, 'YYYY-MM-DD HH24:MI:SS') AND
       operate_time+0 >=
       decode(:X_TAG,
              1,
              TO_DATE(:BEGIN_TIME, 'YYYY-MM-DD HH24:MI:SS'),
              0,
              add_months(TO_DATE(:END_TIME, 'YYYY-MM-DD HH24:MI:SS'),
                         :LIMIT_TIME))