--IS_CACHE=Y
select A.ELEMENT_ID,A.ELEMENT_TYPE_CODE,A.LABEL_ID
  from TD_B_ELEMENT_LABEL A
 where A.ELEMENT_ID = :ELEMENT_ID
   and A.ELEMENT_TYPE_CODE = :ELEMENT_TYPE_CODE
   and A.STATE = '1'
   and exists (select 1
          from TD_B_LABEL B
          where a.label_id=b.label_id
         start with B.LABEL_ID = :LABEL_ID
        connect by prior B.LABEL_ID = B.PARENT_LABEL_ID)