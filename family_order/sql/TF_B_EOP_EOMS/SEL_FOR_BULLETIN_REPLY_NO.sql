select  /*+ use_nl(A B) index(A PK_TF_F_WORKFORM_EOMS)*/
       A.SUB_IBSYSID,
       A.IBSYSID,
       A.GROUP_SEQ,
       A.TRADE_DRIECT,
       to_char(A.INSERT_TIME,'yyyy-MM-dd HH24:mi:ss') INSERT_TIME,
       A.SERIALNO,
       A.OPPERSON,
       A.OPCORP,
       A.OPDEPART,
       A.OPCONTACT,
       '未回复' REPLY_STATE,
       a.RSRV_STR2,
       A.Record_Num, 
       '0' REPLY_CODE
  from TF_B_EOP_EOMS A
 where A.INSERT_TIME between to_date(:BEGIN_DATE, 'yyyy-MM-dd HH24:mi:ss') and
       to_date(:END_DATE, 'yyyy-MM-dd HH24:mi:ss')
   and A.OPER_TYPE = 'newBulletin'
   and A.deal_state = '2'
   and A.RSRV_STR1 = :SYS_TAG
   and not exists (select 1
          from TF_B_EOP_EOMS B
         where A.SUB_IBSYSID = B.SUB_IBSYSID
           and B.OPER_TYPE = 'confirmBulletin'
           and B.DEAL_STATE = '2')
 order by a.insert_time desc