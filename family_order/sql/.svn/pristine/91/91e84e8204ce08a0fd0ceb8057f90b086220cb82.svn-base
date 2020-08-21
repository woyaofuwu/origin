SELECT /*+ index(a,IDX_TL_B_IBTRADE_1)*/transido ,
       hsnduns,
       cutoffday,
       ibbfee/1000 ibbfee, 
       tradestaffid, 
       tradedepartid, 
       DECODE(a.reversedtag,0, '正常','已注销') cancel_tag,
       DECODE(a.bipstatus,'0','成功','1','正在处理','2','失败','3','超时',
                          '4','冲正成功','5','隔日冲正','待处理类业务') bipstatus,
       ibbfeedir
  FROM tl_b_ibtrade a
 WHERE a.bipcode = 'BIP1C001'
   AND a.ibdirtag = '0'   --发起方
   AND a.bipstatus = '0'
   AND a.cutoffday >= TO_DATE(:start_date,'YYYYMMDD')
   AND a.cutoffday < TO_DATE(:end_date,'YYYYMMDD') + 1
   AND a.tradestaffid >= :start_staff_id
   AND a.tradestaffid <= :end_staff_id