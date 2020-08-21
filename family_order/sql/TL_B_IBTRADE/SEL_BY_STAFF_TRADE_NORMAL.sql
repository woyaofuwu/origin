SELECT /*+ index(a,IDX_TL_B_IBTRADE_1)*/transido,
       hsnduns,
       bipcode,
       cutoffday,
       ibafee/1000 ibafee,
       ibsfee/1000 ibsfee,
       tradestaffid, 
       tradedepartid,
       DECODE(a.reversedtag, 0, '正常','已注销') cancel_tag,
       DECODE(a.bipstatus,'0','成功','1','正在处理','2','失败','3','超时',
                          '4','冲正成功','5','隔日冲正','待处理类业务') bipstatus
  FROM tl_b_ibtrade a
 WHERE a.cutoffday >= TO_DATE(:start_date,'YYYYMMDD')
   AND a.cutoffday <  TO_DATE(:end_date,'YYYYMMDD') + 1
   AND a.tradestaffid >= :start_staff_id
   AND a.tradestaffid <= :end_staff_id 
   AND a.ibdirtag = '1' 
   AND a.bipcode NOT IN ('BIP0A001','BIP1C001')