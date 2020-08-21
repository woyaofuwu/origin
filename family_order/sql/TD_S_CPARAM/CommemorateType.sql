SELECT 'CommemorateType' KEY,commemorate_type_code VALUE1,'-1' VALUE2,commemorate_type VRESULT
FROM td_s_commemoratetype WHERE SYSDATE BETWEEN start_date AND end_date AND 'CommemorateType'=:KEY