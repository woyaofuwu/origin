SELECT COUNT(1) recordcount
 FROM
(
select
(SELECT SUM(to_Number(para_code5))
 FROM td_s_commpara
 WHERE param_attr=to_number(:PARAM_ATTR)
   AND param_code=:PARAM_CODE
   AND para_code3 =(
   	   SELECT para_code3 FROM td_s_commpara
	    WHERE param_attr=to_number(:PARAM_ATTR)
		  AND param_code=:PARAM_CODE
		  AND para_code1 = :PARA_CODE1
		  AND subsys_code=:SUBSYS_CODE
		  AND (EPARCHY_CODE=:EPARCHY_CODE OR :EPARCHY_CODE='ZZZZ')
		  AND SYSDATE BETWEEN start_date AND end_date
   )
   AND subsys_code=:SUBSYS_CODE
   AND (EPARCHY_CODE=:EPARCHY_CODE OR :EPARCHY_CODE='ZZZZ')
   AND SYSDATE BETWEEN start_date AND end_date
 ) a,
( SELECT to_Number(para_code4) FROM td_s_commpara
	    WHERE param_attr=to_number(:PARAM_ATTR)
		  AND param_code=:PARAM_CODE
		  AND para_code1 = :PARA_CODE1
		  AND subsys_code= :SUBSYS_CODE
          AND (EPARCHY_CODE=:EPARCHY_CODE OR :EPARCHY_CODE='ZZZZ')
		  AND SYSDATE BETWEEN start_date AND end_date) b
FROM dual
)
WHERE a<b