--IS_CACHE=Y
SELECT STAFF_ID, F_SYS_GETCODENAME('STAFF_ID',STAFF_ID,'','') STAFF_NAME
FROM tf_m_stafffuncright a,td_m_role b
WHERE a.right_attr='1'
  AND a.right_tag='1'
  AND a.right_code=b.role_code
  AND a.right_code=:RIGHT_CODE
 ORDER BY STAFF_ID