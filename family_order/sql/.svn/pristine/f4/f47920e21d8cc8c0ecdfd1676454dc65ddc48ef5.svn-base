SELECT /*+ index(IDX_TL_B_IBTRADE_1)*/bipcode,
       DECODE(bipstatus,'0','成功','1','业务初始态','2','超时','失败') bipstatus, 
       COUNT(*) trade_num
  FROM tl_b_ibtrade
 WHERE cutoffday >= TO_DATE(:start_date,'YYYYMMDD')
   AND cutoffday <  TO_DATE(:end_date,'YYYYMMDD') + 1
   AND testflag = 0  
 GROUP BY bipcode,bipstatus