create table video_actor
(
	actor VARCHAR(20),
	no VARCHAR(20)
		constraint video_actor_video_info_no_fk
			references video_info (no)
)
;

