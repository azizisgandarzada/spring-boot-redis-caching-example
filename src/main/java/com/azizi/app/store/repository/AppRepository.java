package com.azizi.app.store.repository;

import com.azizi.app.store.entity.App;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppRepository extends JpaRepository<App, Integer> {

}
