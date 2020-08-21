INSERT INTO TS_M_IBTRADEFEE_IN
      (ib_sys_id,bip_code,activity_code,transid_o,transid_h,transid_c,
        proc_id,osn_duns,hsn_duns,bip_status,recon_tag,trade_fee,serv_fee,fee_dir,fee_dir_tag,
         eparchy_code,trade_staff_id,trade_depart_id,trade_city_code,trade_eparchy_code,
          cancel_tag,cutoff_day,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_date1,rsrv_date2)
SELECT /*+ index(a,IDX_TL_B_IBTRADE_1)*/ibsysid,bipcode,activitycode,transido,transidh,transidc,
        procid,osnduns,hsnduns,bipstatus,recontag,ibafee,ibsfee,ibafeedir,ibdirtag,
         usereparchycode,tradestaffid,tradedepartid,tradecitycode,tradeeparchycode,
          '0',cutoffday,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_date1,rsrv_date2
  FROM tl_b_ibtrade a
 WHERE a.reversedtag = '0'
   AND a.testflag = '0' 
   AND a.bipcode NOT IN ('BIP0A001','BIP1C001') 
   AND a.cutoffday >= TO_DATE(:clct_day,'YYYYMMDD')
   AND a.cutoffday <  TO_DATE(:clct_day,'YYYYMMDD') + 1
   AND a.tradestaffid >= :start_staff_id
   AND a.tradestaffid <= :end_staff_id