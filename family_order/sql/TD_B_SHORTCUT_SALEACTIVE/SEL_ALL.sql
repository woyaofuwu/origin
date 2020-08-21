--IS_CACHE=Y
select A.PRODUCT_ID,
       A.PACKAGE_ID,
       A.SHORTCUT_TYPE,
       A.PACKAGE_NAME,
       C.LABEL_ID
  from TD_B_SHORTCUT_SALEACTIVE A, TD_B_ELEMENT_LABEL C
 where A.PRODUCT_ID = C.ELEMENT_ID
   and C.ELEMENT_TYPE_CODE = 'P'
   and sysdate between START_DATE and END_DATE
   and exists (select 1
          from TD_B_LABEL B
         where C.LABEL_ID = B.LABEL_ID
         start with B.LABEL_ID = 'L000'
        connect by prior B.LABEL_ID = B.PARENT_LABEL_ID)

