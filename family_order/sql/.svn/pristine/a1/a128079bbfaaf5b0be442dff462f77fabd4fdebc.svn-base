SELECT /*+ index(a,IDX_TL_B_IBTRADE_1)*/a.transido,
       a.hsnduns,
       a.cutoffday,
       -b.ibbfee/1000 ibbfee, 
       a.tradestaffid, 
       a.tradedepartid, 
       '注销他人'  cancel_tag,
       DECODE(a.bipstatus,'0','成功','1','正在处理','2','失败','3','超时',
                          '4','冲正成功','5','隔日冲正','待处理类业务') bipstatus,
       a.ibbfeedir
  FROM tl_b_ibtrade a,tl_b_ibtrade b
 WHERE a.cutoffday >= TO_DATE(:start_date,'YYYYMMDD')
   AND a.cutoffday < TO_DATE(:end_date,'YYYYMMDD') + 1
   AND a.tradestaffid >= :start_staff_id
   AND a.tradestaffid <= :end_staff_id 
   AND a.bipcode = 'BIP0A001'   --冲正记录
   AND a.bipstatus = '0'           --冲正成功
   AND a.ibdirtag = '0'         --发起方
   AND a.procid = b.procid
   AND a.transido = b.transido
   AND b.reversedtag = '1' -- 返销记录
   AND a.tradestaffid != b.tradestaffid  -- 返销他人
   AND b.bipcode != 'BIP0A001'