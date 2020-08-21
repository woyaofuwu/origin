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
       dpt.depart_name as parent_depart_name
  from TF_F_BUYOUT_TELEQU t, tf_chl_channel chl,
  (select d.depart_id,d.depart_code,d.depart_name,d.area_code
   from td_m_depart d
   where 1=1
   and d.depart_id=:DEPART_ID
  ) dpt
  where t.chnl_id = chl.chnl_id
  and chl.city_code = dpt.area_code
  and chl.chnl_id=:CHNL_ID
  and t.reg_date between to_date(:START_REG_DATE,'yyyy-mm-dd hh24:mi:ss') and to_date(:END_REG_DATE,'yyyy-mm-dd hh24:mi:ss')