package com.azizi.app.store.repository;

import com.azizi.app.store.entity.App;
import com.azizi.app.store.entity.Downloader;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DownloaderRepository extends JpaRepository<Downloader, Integer> {

    void deleteAllByApp(App app);

}
