create table video_category
(
	category VARCHAR(20),
	no VARCHAR(20)
		constraint video_category_video_info_no_fk
			references video_info (no)
)
;

