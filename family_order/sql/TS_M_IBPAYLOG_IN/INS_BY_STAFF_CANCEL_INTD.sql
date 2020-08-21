INSERT INTO TS_M_IBPAYLOG_IN
      (ib_sys_id,bip_code,transid_o,transid_h,transid_c,
        proc_id,osn_duns,hsn_duns,bip_status,recon_tag,recv_fee,fee_dir,fee_dir_tag,
         eparchy_code,recv_staff_id,recv_depart_id,recv_city_code,recv_eparchy_code,
          cancel_tag,cutoff_day,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_date1,rsrv_date2)
SELECT /*+ index(a,IDX_TL_B_IBTRADE_1)*/b.ibsysid,b.bipcode,b.transido,b.transidh,b.transidc,
        b.procid,b.osnduns,b.hsnduns,b.bipstatus,b.recontag,b.ibbfee,b.ibbfeedir,b.ibdirtag,
         b.tradeeparchycode,a.tradestaffid,a.tradedepartid,a.tradecitycode,a.tradeeparchycode,
          '1',b.cutoffday,b.rsrv_str1,b.rsrv_str2,b.rsrv_str3,b.rsrv_date1,b.rsrv_date2
  FROM tl_b_ibtrade a ,tl_b_ibtrade b
 WHERE a.reversedtag = '1'
   AND b.bipcode = 'BIP0A001'
   AND b.activitycode = 'T1001001'
   AND a.testflag = '0'
   AND a.procid = b.procid
   AND a.transido = b.transido
   AND b.reversedtag = '0' 
   AND a.cutoffday >= TO_DATE(:clct_day,'YYYYMMDD')
   AND a.cutoffday <  TO_DATE(:clct_day,'YYYYMMDD') + 1
   AND a.tradestaffid >= :start_staff_id
   AND a.tradestaffid <= :end_staff_id