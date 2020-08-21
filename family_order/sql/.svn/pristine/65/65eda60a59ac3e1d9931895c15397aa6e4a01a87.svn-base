select chl.chnl_id,chl.chnl_code,chl.chnl_name
  from tf_chl_channel chl,
  (select depart_id,depart_code,depart_name,area_code from 
   td_m_depart
   where 1=1
   and depart_id=:DEPART_ID
  ) dpt
  where chl.city_code=dpt.area_code
  and chl.chnl_name like '%' || :CHNL_NAME || '%'
  and chl.chnl_code=:CHNL_CODE