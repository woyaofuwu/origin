INSERT INTO TS_S_IBTFEE_DAY_STAFF
      (clct_day,date_id,staff_id,depart_id,city_code,eparchy_code,eparchy_code_ed,
        bip_code,activity_code,hsn_duns,osn_duns,fee_dir,fee_dir_tag,
         sale_num,sale_money,serv_money,cancel_tag,bip_status)
SELECT :clct_day,TO_NUMBER(SUBSTR(:clct_day,5,2)),a.trade_staff_id,a.trade_depart_id,a.trade_city_code,a.trade_eparchy_code,a.eparchy_code,
        a.bip_code,a.activity_code,a.hsn_duns,a.osn_duns,a.fee_dir,a.fee_dir_tag,
         -COUNT(a.ib_sys_id),-SUM(a.trade_fee),-SUM(a.serv_fee),cancel_tag,a.bip_status
  FROM ts_m_ibtradefee_in a
 WHERE a.cancel_tag != '0'  
 GROUP BY a.trade_staff_id,a.trade_depart_id,a.trade_city_code,a.trade_eparchy_code,a.eparchy_code,
           a.bip_code,a.activity_code,a.hsn_duns,a.osn_duns,a.fee_dir,a.fee_dir_tag,cancel_tag,a.bip_status