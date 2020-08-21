--IS_CACHE=Y
SELECT 'VipClass' KEY, vip_type_code VALUE1,class_id VALUE2,class_name VRESULT
FROM td_m_vipclass WHERE 'VipClass'=:KEY