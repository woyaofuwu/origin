--IS_CACHE=Y
SELECT 'UserType' KEY,user_type_code VALUE1,eparchy_code VALUE2,user_type VRESULT FROM td_b_usertype
 WHERE user_attr='0' AND SYSDATE BETWEEN start_date AND end_date AND 'UserType'=:KEY