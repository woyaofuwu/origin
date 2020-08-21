SELECT COUNT(*) recordcount
  FROM tl_b_import_log
 WHERE (serial_number =:PARAM0 or :PARAM0 is null)
   and (group_id =:PARAM1 or :PARAM1 is null)
   and (in_staff_id =:PARAM2 or :PARAM2 is null)
   and error_info like '%'||:PARAM3||'%'
   AND ( in_date>=TO_DATE(:PARAM4 , 'YYYY-MM-DD HH24:MI:SS') OR :PARAM4 IS NULL )
   and ( in_date<=TO_DATE(:PARAM5 , 'YYYY-MM-DD HH24:MI:SS') OR :PARAM5 IS NULL)
   and prevalue1=:PARAM6