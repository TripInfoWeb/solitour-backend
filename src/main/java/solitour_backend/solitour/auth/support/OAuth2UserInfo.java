package solitour_backend.solitour.auth.support;

public interface OAuth2UserInfo {

  String getProviderId();

  String getProvider();

  String getEmail();

  String getName();
}
