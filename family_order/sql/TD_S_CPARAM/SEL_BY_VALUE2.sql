--IS_CACHE=Y
SELECT t.key,t.value1,t.value2,t.VRESULT FROM td_s_cparam t
WHERE t.KEY = :KEY
AND T.VALUE1 = :VALUE1
AND t.value2 = :VALUE2