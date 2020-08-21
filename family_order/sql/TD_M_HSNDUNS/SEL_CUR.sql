--IS_CACHE=Y
SELECT sys_code,domain,hsn_duns,ib_province_no,province_no,this_tag,order_no,remark 
FROM td_m_hsnduns 
WHERE this_tag = '1'