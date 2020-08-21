Select COUNT(1) recordcount
FROM tf_f_user_sale_active  a ,td_b_package b
WHERE  user_id = TO_NUMBER(:USER_ID)
And  a.partition_id =Mod(TO_NUMBER(:USER_ID),10000)
And a.package_id=b.package_id
And (b.package_kind_code  =:RSRV_STR1 OR :RSRV_STR1 IS NULL OR b.package_kind_code  like :RSRV_STR1)
AND (a.rsrv_str2=:RSRV_STR2 OR :RSRV_STR2 IS NULL)
AND (a.rsrv_str3=:RSRV_STR3 OR :RSRV_STR3 IS NULL)
AND (a.rsrv_str4=:RSRV_STR4 OR :RSRV_STR4 IS NULL)
AND (a.rsrv_str5=:RSRV_STR5 OR :RSRV_STR5 IS NULL)
AND   a.end_date > trunc(last_day(SYSDATE)) + 1