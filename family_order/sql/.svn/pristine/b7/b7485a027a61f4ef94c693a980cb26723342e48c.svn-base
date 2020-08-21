--IS_CACHE=Y
SELECT *
  FROM td_user_channel
 WHERE begin_partition_id<=:VBEGIN_PARTITION_ID
   AND end_partition_id>=:VEND_PARTITION_ID
   AND eparchy_code=:VEPARCHY_CODE