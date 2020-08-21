DELETE FROM td_m_right_relation
 WHERE eparchy_code=:EPARCHY_CODE
   AND right_code_a=:RIGHT_CODE_A
   AND right_code_b=:RIGHT_CODE_B
   AND (:DATA_TYPE IS NULL OR data_type=:DATA_TYPE)