INSERT INTO TS_M_IBPAYLOG_IN
      (ib_sys_id,bip_code,transid_o,transid_h,transid_c,
        proc_id,osn_duns,hsn_duns,bip_status,recon_tag,recv_fee,fee_dir,fee_dir_tag,
         eparchy_code,recv_staff_id,recv_depart_id,recv_city_code,recv_eparchy_code,
          cancel_tag,cutoff_day,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_date1,rsrv_date2)
SELECT /*+ index(a,IDX_TL_B_IBTRADE_1)*/ibsysid,bipcode,transido,transidh,transidc,
        procid,osnduns,hsnduns,bipstatus,recontag,ibbfee,ibbfeedir,ibdirtag,
         usereparchycode,tradestaffid,tradedepartid,tradecitycode,tradeeparchycode,
          '0',cutoffday,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_date1,rsrv_date2
  FROM tl_b_ibtrade a
 WHERE a.reversedtag = '0'
   AND testflag = '0' 
   AND a.bipcode IN ('BIP1C001')
   AND a.activitycode = 'T1001001'
   AND a.cutoffday >= TO_DATE(:clct_day,'YYYYMMDD')
   AND a.cutoffday <  TO_DATE(:clct_day,'YYYYMMDD') + 1
   AND a.tradedepartid = :depart_id