--IS_CACHE=Y  
SELECT mgmt_district eparchy_code,hlr_seg moffice_id,h_flag switch_id,start_num serialnumber_s,end_num serialnumber_e,'' imsi_s,'' imsi_e,
to_char(done_time,'yyyy-mm-dd hh24:mi:ss') update_time,op_id update_staff_id,org_id update_depart_id
  FROM res_numseg_hlr
 WHERE start_num<=:SERIAL_NUMBER
   AND end_num>=:SERIAL_NUMBER