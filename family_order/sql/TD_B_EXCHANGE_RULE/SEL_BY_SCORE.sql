--IS_CACHE=Y
SELECT A.SCORE SCORE_TYPE,A.SCORE FROM TD_B_EXCHANGE_RULE A
WHERE A.STATUS = '0'
AND (A.EPARCHY_CODE = :EPARCHY_CODE OR A.EPARCHY_CODE = 'ZZZZ')
AND (A.BRAND_CODE = :BRAND_CODE OR A.BRAND_CODE = 'G000' OR A.BRAND_CODE = 'ZZZZ')
GROUP BY A.SCORE
ORDER BY A.SCORE