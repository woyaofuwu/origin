UPDATE td_s_commpara
SET para_code3 = :PARA_CODE3,para_code17 = :PARA_CODE7,
para_code18 = :PARA_CODE4,para_code19 = :PARA_CODE2,
para_code21 = :PARA_CODE5,para_code20 = :PARA_CODE6
WHERE subsys_code = 'CSM'
AND param_attr = 741
AND para_code1 = :PARA_CODE1
AND (:PARA_CODE8 = '' OR :PARA_CODE8 IS NULL)
AND (:PARA_CODE9 = '' OR :PARA_CODE9 IS NULL)
AND (:PARA_CODE10 = '' OR :PARA_CODE10 IS NULL)