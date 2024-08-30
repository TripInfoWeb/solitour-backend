package solitour_backend.solitour.user_image.entity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserImageRepository extends JpaRepository<UserImage, Long> {

    UserImage save(UserImage userImage);
}
