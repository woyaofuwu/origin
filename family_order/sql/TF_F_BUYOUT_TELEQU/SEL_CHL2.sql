select chl.chnl_id,chl.chnl_code,chl.chnl_name
  from tf_chl_channel chl
  where 1=1
  and chl.chnl_name like '%' || :CHNL_NAME || '%'
  and chl.chnl_code=:CHNL_CODE