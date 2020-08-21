SELECT '1' JIANDANG_NUM,
       a.bi_sn,
       a.eparchy_code,
       a.update_staff_id,
       a.city_code
FROM TF_BH_EWE a
WHERE a.BPM_TEMPLET_ID = 'INTEGRATIONOPEN'
AND a.STATE = '9'
AND a.UPDATE_DATE >= to_date(:BEGIN_DATE,'yyyy-MM-dd HH24:mi:ss')
AND a.UPDATE_DATE <= to_date(:END_DATE,'yyyy-MM-dd HH24:mi:ss')
AND (a.EPARCHY_CODE = :EPARCHY_CODE OR a.CITY_CODE = :EPARCHY_CODE OR :EPARCHY_CODE = 'XINJ')