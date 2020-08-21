SELECT partition_id,to_char(user_id) user_id,to_char(user_id_a) user_id_a,a.discnt_code,spec_tag,relation_type_code,to_char(a.start_date,'yyyy-mm-dd hh24:mi:ss') start_date,to_char(a.end_date,'yyyy-mm-dd hh24:mi:ss') end_date
FROM tf_f_user_discnt a
WHERE a.user_id=:USER_ID
  AND a.partition_id=MOD(:USER_ID,10000)
  AND sysdate < a.end_date
  AND EXISTS(SELECT 1 FROM td_s_discnt_svc_limit b
             WHERE a.discnt_code=b.discnt_code
             AND b.service_id=:SERVICE_ID
             AND (b.limit_tag=:LIMIT_TAG OR :LIMIT_TAG IS NULL)
             AND sysdate<b.end_date
             AND b.eparchy_code=:EPARCHY_CODE OR b.eparchy_code='ZZZZ'
            )