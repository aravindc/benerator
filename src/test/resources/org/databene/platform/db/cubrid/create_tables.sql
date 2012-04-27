create table chartest (
	char_col          char(4) not null,
	varchar_col       varchar(8) not null,
	nchar_col         nchar(6) not null,
	nchar_varying_col nchar varying(10) not null
);

create table lobtest (
	clob_col clob,
	blob_col blob
);

create table numtest (
	smallint_col smallint not null,
	int_col      int not null,
	bigint_col   bigint not null,
	decimal_col  decimal not null,
	real_col     real not null,
	double_col   double not null,
	monetary_col monetary not null
);

create table datetimetest (
	date_col      date not null,
	time_col      time not null,
	timestamp_col timestamp not null,
	datetime_col  datetime not null
);

create table pktest (
	pk int not null,
	primary key (pk)
);

create table uktest (
	number int unique not null
);

create table fktest (
	fk int not null,
	foreign key (fk) references pktest (pk)
);

create table nulltest (
	not_null_col int not null,
	null_col     int null
);
