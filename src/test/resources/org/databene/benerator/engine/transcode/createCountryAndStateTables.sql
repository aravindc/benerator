create table COUNTRY (
  ID     int         not null,
  NAME   varchar(30) not null,
  constraint COUNTRY_PK primary key (ID)
);

create table STATE (
  ID               int         not null,
  COUNTRY_FK       int,
  NAME             varchar(30) not null,
  constraint STATE_PK primary key (ID),
  constraint STATE_COUNTRY_FK foreign key (COUNTRY_FK) references COUNTRY (ID)
);
