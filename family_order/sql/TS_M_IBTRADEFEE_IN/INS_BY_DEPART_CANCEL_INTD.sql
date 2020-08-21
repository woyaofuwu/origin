INSERT INTO TS_M_IBTRADEFEE_IN
      (ib_sys_id,bip_code,activity_code,transid_o,transid_h,transid_c,
        proc_id,osn_duns,hsn_duns,bip_status,recon_tag,trade_fee,serv_fee,fee_dir,fee_dir_tag,
         eparchy_code,trade_staff_id,trade_depart_id,trade_city_code,trade_eparchy_code,
          cancel_tag,cutoff_day,rsrv_str1,rsrv_str2,rsrv_str3,rsrv_date1,rsrv_date2)
SELECT /*+ index(a,IDX_TL_B_IBTRADE_1) index(b,IDX_TL_B_IBTRADE_1)*/b.ibsysid,b.bipcode,b.activitycode,b.transido,b.transidh,b.transidc,
        b.procid,b.osnduns,b.hsnduns,b.bipstatus,b.recontag,b.ibafee,b.ibsfee,b.ibafeedir,b.ibdirtag,
         b.usereparchycode,a.tradestaffid,a.tradedepartid,a.tradecitycode,a.tradeeparchycode,
          '1',b.cutoffday,b.rsrv_str1,b.rsrv_str2,b.rsrv_str3,b.rsrv_date1,b.rsrv_date2
  FROM tl_b_ibtrade a,tl_b_ibtrade b
 WHERE a.reversedtag = '1'
   AND a.bipcode = 'BIP0A001'
   AND a.testflag = '0'
   AND a.cutoffday >= TO_DATE(:clct_day,'YYYYMMDD')
   AND a.cutoffday <  TO_DATE(:clct_day,'YYYYMMDD') + 1
   AND a.tradedepartid = :depart_id 
   AND a.procid = b.procid
   AND a.transido = b.transido   
   AND b.bipcode NOT IN ('BIP0A001','BIP1C001')  
   AND b.reversedtag = '0'
   AND b.testflag = '0'
   AND b.cutoffday >= TO_DATE(:clct_day,'YYYYMMDD')
   AND B.cutoffday <  TO_DATE(:clct_day,'YYYYMMDD') + 1