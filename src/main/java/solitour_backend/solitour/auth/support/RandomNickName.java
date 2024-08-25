package solitour_backend.solitour.auth.support;

import java.util.UUID;

public class RandomNickName {

    public static String generateRandomNickname() {
        UUID uuid = UUID.randomUUID();
        String nickname = uuid.toString().substring(0, 4);
        return "유저" + nickname;
    }

}
