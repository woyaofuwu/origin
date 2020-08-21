SELECT to_char(operate_id) operate_id,
       operate_type,
       partition_id,
       to_char(acct_id) acct_id,
       deposit_code,
       access_tag,
       to_char(nvl(money,'0')) money,
       to_char(nvl(old_balance,'0')) old_balance,
       to_char(nvl(new_balance,'0')) new_balance,
       cancel_tag,
       eparchy_code,
       to_char(operate_time,'yyyy-mm-dd hh24:mi:ss') operate_time
  FROM tf_ahb_accesslog_2006
 WHERE partition_id >= :PARTITION_ID1 and partition_id <= :PARTITION_ID2 AND
       acct_id = TO_NUMBER(:ACCT_ID) AND
       trim(eparchy_code) = :EPARCHY_CODE

UNION ALL
SELECT to_char(operate_id) operate_id,
       operate_type,
       partition_id,
       to_char(acct_id) acct_id,
       deposit_code,
       access_tag,
       to_char(nvl(money,'0')) money,
       to_char(nvl(old_balance,'0')) old_balance,
       to_char(nvl(new_balance,'0')) new_balance,
       cancel_tag,
       eparchy_code,
       to_char(operate_time,'yyyy-mm-dd hh24:mi:ss') operate_time
  FROM tf_ahb_accesslog_d_2006
 WHERE partition_id >= :PARTITION_ID1 and partition_id <= :PARTITION_ID2 AND
       acct_id = TO_NUMBER(:ACCT_ID) AND
       trim(eparchy_code) = :EPARCHY_CODE