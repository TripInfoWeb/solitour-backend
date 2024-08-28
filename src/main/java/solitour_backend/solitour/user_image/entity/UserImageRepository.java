package solitour_backend.solitour.user_image.entity;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

public interface UserImageRepository extends Repository<UserImage, Long> {

    UserImage save(UserImage userImage);
}
