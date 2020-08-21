--IS_CACHE=Y
SELECT label_name FROM td_m_mustfillitem
 WHERE form_name = :FORM_NAME
   AND (eparchy_code = :EPARCHY_CODE
    OR (eparchy_code = 'ZZZZ'
   AND NOT EXISTS (SELECT 1 FROM td_m_mustfillitem
                    WHERE form_name = :FORM_NAME
                      AND eparchy_code = :EPARCHY_CODE)))