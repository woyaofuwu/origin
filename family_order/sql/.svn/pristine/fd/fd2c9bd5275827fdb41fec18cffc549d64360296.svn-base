SELECT /*+ index(a,IDX_TL_B_IBTRADE_1)*/a.transido,
       a.hsnduns,
       a.bipcode,
       a.cutoffday,
       -a.ibafee/1000 ibafee,
       -a.ibsfee/1000 ibafee,
       a.tradestaffid,
       a.tradedepartid,
       '注销本人' cancel_tag,
       DECODE(a.bipstatus,'0','成功','1','正在处理','2','失败','3','超时',
                          '4','冲正成功','5','隔日冲正','待处理类业务') bipstatus
  FROM tl_b_ibtrade a,tl_b_ibtrade b
 WHERE a.reversedtag = '1'
   AND a.bipcode = 'BIP0A001'   --冲正记录
   AND a.testflag = '0'
   AND a.ibdirtag = '0'         --发起方
   AND a.bipstatus = '0'           --冲正成功 
   AND a.cutoffday >= TO_DATE(:start_date,'YYYYMMDD')
   AND a.cutoffday < TO_DATE(:end_date,'YYYYMMDD') + 1
   AND a.tradestaffid >= :start_staff_id
   AND a.tradestaffid <= :end_staff_id
   AND a.procid = b.procid
   AND a.transido = b.transido 
   AND a.tradestaffid = b.tradestaffid
   AND b.reversedtag = '0'
   AND b.bipcode NOT IN ('BIP0A001','BIP1C001')
   AND b.testflag = '0'