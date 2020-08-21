INSERT INTO TS_S_IBPFEE_DAY_STAFF
      (clct_day,date_id,staff_id,depart_id,city_code,eparchy_code,eparchy_code_ed,
        hsn_duns,osn_duns,fee_dir,fee_dir_tag,sale_num,sale_money,cancel_tag,bip_status)
SELECT :clct_day,TO_NUMBER(SUBSTR(:clct_day,5,2)),a.recv_staff_id,a.recv_depart_id,a.recv_city_code,a.recv_eparchy_code,a.eparchy_code,
        a.hsn_duns,a.osn_duns,a.fee_dir,a.fee_dir_tag,COUNT(a.ib_sys_id),SUM(a.recv_fee),'0',a.bip_status
  FROM ts_m_ibpaylog_in a
 WHERE a.cancel_tag = '0'
 GROUP BY a.recv_staff_id,a.recv_depart_id,a.recv_city_code,a.recv_eparchy_code,a.eparchy_code,
           a.hsn_duns,a.osn_duns,a.fee_dir,a.fee_dir_tag,a.bip_status