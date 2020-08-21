update TF_F_WIDENET_BOOK t set t.rsrv_tag3 = :REG_STATUS
where t.HOME_ADDR = (select HOME_ADDR from TF_F_WIDENET_BOOK where inst_id = :INST_ID)