SELECT COUNT(1) recordcount
  FROM TF_F_USER_SALE_ACTIVE
 WHERE PARTITION_ID = MOD(TO_NUMBER(:USER_ID),10000)
   AND user_id = TO_NUMBER(:USER_ID)
   AND (nvl(RSRV_STR10,'*') != 'r' OR nvl(RSRV_STR5,'*') != 'd')
   AND nvl(RSRV_STR10,'*') != '1+'
   AND PROCESS_TAG = '0'
   and nvl(rsrv_date2,end_date) > sysdate