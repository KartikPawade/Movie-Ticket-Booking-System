create index idx_screen_theatreId on screen(theatre_id);
create index idx_show_screenId on show(screen_id);  --can add  movieId later
--create index idx_theatre_cityId on theatre(city_id); --- dont need this, having less data
--create index idx_seat_screenId on seat(screen_id); ---dont need this,having less data
create index idx_bookingDetails_showId on booking_details(show_id); -- for now, later can include seatId if needed