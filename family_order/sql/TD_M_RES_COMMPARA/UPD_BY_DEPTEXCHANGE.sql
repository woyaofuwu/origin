UPDATE td_m_res_commpara
set para_value2 = :NUM,remark = :REMARK
where eparchy_code = :EPARCHY_CODE
  and para_attr = :PARA_ATTR
  and para_code1 = :PARA_CODE1
  and para_code2 = :PARA_CODE2