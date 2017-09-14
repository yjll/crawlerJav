create table video_info
(
	no varchar(20) not null
		primary key,
	title varchar(500),
	date varchar(20),
	duration int(10),
	rated float,
	system_time datetime
)
;

