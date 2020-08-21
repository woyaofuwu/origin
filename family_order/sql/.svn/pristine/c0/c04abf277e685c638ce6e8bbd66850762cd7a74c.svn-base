select t.log_id,
       t.telequ_price,
       t.telequ_count,
       t.telequ_fee_totle,
       t.chnl_id,
       t.reg_date,
       t.reg_staff_id,
       t.reg_depart_id,
       t.update_time,
       t.update_staff_id,
       t.update_depart_id,
       t.remark,
       t.rsrv_num1,
       t.rsrv_num2,
       t.rsrv_num3,
       t.rsrv_str1,
       t.rsrv_str2,
       t.rsrv_str3,
       t.rsrv_date1,
       t.rsrv_date2,
       t.rsrv_date3,
       t.rsrv_tag1,
       t.rsrv_tag2,
       t.rsrv_tag3,
       chl.chnl_name,
       dept.depart_name as parent_depart_name
  from TF_F_BUYOUT_TELEQU t, td_m_depart dpt, td_m_depart dept, tf_chl_channel chl
  where t.chnl_id = chl.chnl_id
  and t.reg_depart_id = dpt.depart_id
  and dpt.parent_depart_id = dept.depart_id
  and chl.chnl_id = :CHNL_ID
  and t.reg_date between to_date(:START_REG_DATE,'yyyy-mm-dd hh24:mi:ss') and to_date(:END_REG_DATE,'yyyy-mm-dd hh24:mi:ss')