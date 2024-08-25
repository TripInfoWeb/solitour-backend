package solitour_backend.solitour.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import solitour_backend.solitour.admin.entity.Banner;
import solitour_backend.solitour.admin.service.BannerService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/banner")
public class BannerController {
    private final BannerService bannerService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createBanner(@RequestPart("imageFile") MultipartFile imageFile,
                                       @RequestPart("directory") String directory) {
        Banner banner = bannerService.createBanner(imageFile, directory);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(banner);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getBannerList() {
        return bannerService.getBannerList();
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    public ResponseEntity<String> deleteBanner(@PathVariable Long id) {
        return bannerService.deleteBanner(id);
    }

}
