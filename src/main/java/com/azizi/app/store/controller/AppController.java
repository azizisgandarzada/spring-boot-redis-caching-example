package com.azizi.app.store.controller;

import com.azizi.app.store.dto.AppDto;
import com.azizi.app.store.dto.PageDto;
import com.azizi.app.store.dto.UserDto;
import com.azizi.app.store.service.AppService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("apps")
@RequiredArgsConstructor
public class AppController {

    private final AppService appService;

    @GetMapping
    public PageDto<AppDto> getApps(Pageable pageable) {
        return appService.getApps(pageable);
    }

    @GetMapping("{id}")
    public AppDto getAppDetails(@PathVariable Integer id) {
        return appService.getAppDetails(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppDto publishApp(@RequestBody @Valid AppDto appDto) {
        return appService.publishApp(appDto);
    }

    @PutMapping("{id}")
    public AppDto updateApp(@PathVariable Integer id, @RequestBody @Valid AppDto appDto) {
        return appService.updateApp(id, appDto);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Integer id) {
        appService.deleteApp(id);
    }

    @PostMapping("downloading/{appId}/{userId}")
    public Boolean downloadApp(@PathVariable Integer appId, @PathVariable Integer userId) {
        return appService.downloadApp(appId, userId);
    }

    @GetMapping("downloads/{appId}")
    public PageDto<UserDto> getDownloads(@PathVariable Integer appId, Pageable pageable) {
        return appService.getDownloads(appId, pageable);
    }

}
