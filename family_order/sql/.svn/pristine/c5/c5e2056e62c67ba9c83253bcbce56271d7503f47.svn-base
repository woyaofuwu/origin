UPDATE td_s_commpara
SET para_code3 = '0',para_code17 = '',para_code18 = '',para_code19 = '',
para_code20 = '',para_code21 = '',para_code22 = ''
WHERE subsys_code = 'CSM'
AND param_attr = 741
AND para_code3 <> '0'
AND ((sysdate- to_date(para_code17,'yyyy-mm-dd hh24:mi:ss'))>10)
AND (:PARA_CODE1 = '' OR :PARA_CODE1 IS NULL)
AND (:PARA_CODE2 = '' OR :PARA_CODE2 IS NULL)
AND (:PARA_CODE3 = '' OR :PARA_CODE3 IS NULL)
AND (:PARA_CODE4 = '' OR :PARA_CODE4 IS NULL)
AND (:PARA_CODE5 = '' OR :PARA_CODE5 IS NULL)
AND (:PARA_CODE6 = '' OR :PARA_CODE6 IS NULL)
AND (:PARA_CODE7 = '' OR :PARA_CODE7 IS NULL)
AND (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)
AND (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
AND (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)