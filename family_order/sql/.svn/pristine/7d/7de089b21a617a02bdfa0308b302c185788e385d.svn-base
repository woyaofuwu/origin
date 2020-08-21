select t1.ATTR_CODE, t1.ATTR_VALUE, t1.PARENT_ATTR_CODE, t1.RECORD_NUM
  from TF_B_EOP_ATTR t1,
       (select MAX(a.SEQ) as SEQ,a.ATTR_CODE
          from TF_B_EOP_ATTR a
         where a.IBSYSID = :IBSYSID
         group by a.ATTR_CODE) t2
 where t1.SEQ = t2.SEQ
   and t1.ATTR_CODE = t2.ATTR_CODE
   and t1.IBSYSID = :IBSYSID