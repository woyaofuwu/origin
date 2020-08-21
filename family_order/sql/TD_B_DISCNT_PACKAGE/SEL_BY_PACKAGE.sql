--IS_CACHE=Y
SELECT DISTINCT(package_id) package_id,package_name 
  FROM td_b_discnt_package
 WHERE (:PACKAGE_TYPE IS NULL OR package_type=:PACKAGE_TYPE)
   AND eparchy_code=:EPARCHY_CODE
   AND start_date<=SYSDATE 
   AND end_date>=SYSDATE
 ORDER BY package_id