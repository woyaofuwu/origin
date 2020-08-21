SELECT /*+ index(b,IDX_TL_B_IBTRADE_1)*/a.transido,
       a.hsnduns,
       a.cutoffday, 
       -b.ibbfee/1000 ibbfee, 
       b.tradestaffid, 
       b.tradedepartid,
        '注销本人' cancel_tag,
       DECODE(a.bipstatus,'0','成功','1','正在处理','2','失败','3','超时',
                          '4','冲正成功','5','隔日冲正','待处理类业务') bipstatus,
       a.ibbfeedir
  FROM tl_b_ibtrade a,tl_b_ibtrade b
 WHERE b.reversedtag = '1'
   AND b.bipcode = 'BIP0A001' 
   AND b.cutoffday >= TO_DATE(:start_date,'YYYYMMDD')
   AND b.cutoffday < TO_DATE(:end_date,'YYYYMMDD') + 1
   AND b.tradestaffid >= :start_staff_id
   AND b.tradestaffid <= :end_staff_id
   AND a.bipstatus = '0'
   AND b.bipstatus = '0'
   AND b.ibdirtag = '0'
   AND a.procid = b.procid
   AND a.transido = b.transido
   AND a.tradestaffid = b.tradestaffid
   AND a.reversedtag = '0'
   AND a.bipcode NOT IN ('BIP0A001','BIP1C001')