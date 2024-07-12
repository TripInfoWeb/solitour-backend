package solitour_backend.solitour.info_tag.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import solitour_backend.solitour.info_tag.repository.InfoTagRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class InfoTagService {

  private final InfoTagRepository infoTagRepository;

}
