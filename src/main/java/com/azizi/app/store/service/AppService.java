package com.azizi.app.store.service;

import com.azizi.app.store.constant.RedisCacheKeyConstants;
import com.azizi.app.store.dto.AppDto;
import com.azizi.app.store.dto.PageDto;
import com.azizi.app.store.dto.UserDto;
import com.azizi.app.store.entity.App;
import com.azizi.app.store.entity.Category;
import com.azizi.app.store.entity.Downloader;
import com.azizi.app.store.entity.User;
import com.azizi.app.store.exception.AppNotFoundException;
import com.azizi.app.store.exception.CategoryNotFoundException;
import com.azizi.app.store.exception.UserNotFoundException;
import com.azizi.app.store.mapper.AppMapper;
import com.azizi.app.store.mapper.UserMapper;
import com.azizi.app.store.repository.AppRepository;
import com.azizi.app.store.repository.CategoryRepository;
import com.azizi.app.store.repository.DownloaderRepository;
import com.azizi.app.store.repository.UserRepository;
import com.github.javafaker.Faker;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.PostConstruct;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppService {

    private final AppRepository appRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final DownloaderRepository downloaderRepository;
    private final UserMapper userMapper;
    private final AppMapper appMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, Integer, Object> hashOperations;
    private final ListOperations<String, Object> listOperations;
    private final ZSetOperations<String, Object> zSetOperations;
    private final Random random;

    public AppService(AppRepository appRepository, CategoryRepository categoryRepository,
                      UserRepository userRepository, DownloaderRepository downloaderRepository, UserMapper userMapper,
                      AppMapper appMapper, RedisTemplate<String, Object> redisTemplate) throws NoSuchAlgorithmException {
        this.appRepository = appRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.downloaderRepository = downloaderRepository;
        this.userMapper = userMapper;
        this.appMapper = appMapper;
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
        this.listOperations = redisTemplate.opsForList();
        this.zSetOperations = redisTemplate.opsForZSet();
        this.random = SecureRandom.getInstanceStrong();
    }

    @PostConstruct
    public void init() {
        if (!categoryRepository.findAll().isEmpty()) {
            return;
        }
        downloaderRepository.deleteAll();
        userRepository.deleteAll();
        appRepository.deleteAll();
        categoryRepository.deleteAll();
        Faker faker = new Faker();
        List<Category> categories = createCategories();
        List<User> users = createUsers(faker);
        List<App> apps = createApps(faker, categories);
        createDownloads(apps, users);
    }

    public PageDto<AppDto> getApps(Pageable pageable) {
        Set<Object> appIds = zSetOperations.range(RedisCacheKeyConstants.APPS_ZSET_KEY, pageable.getOffset(),
                pageable.getOffset() + pageable.getPageSize() - 1);
        if (appIds != null) {
            List<AppDto> users = appIds.stream()
                    .map(appId -> (AppDto) hashOperations.get(RedisCacheKeyConstants.APP_HASH_KEY, appId))
                    .collect(Collectors.toList());
            Long count = zSetOperations.size(RedisCacheKeyConstants.APPS_ZSET_KEY);
            return PageDto.of(users, pageable.getPageNumber(), pageable.getPageSize(), count);
        }
        return PageDto.empty();
    }

    public AppDto getAppDetails(Integer id) {
        AppDto appDto = (AppDto) hashOperations.get(RedisCacheKeyConstants.APP_HASH_KEY, id);
        if (appDto == null) {
            throw new AppNotFoundException();
        }
        return appDto;
    }

    @Transactional
    public AppDto publishApp(AppDto appDto) {
        Category category = categoryRepository.findById(appDto.getCategory().getId())
                .orElseThrow(CategoryNotFoundException::new);
        App app = appMapper.toEntity(appDto);
        app.setCategory(category);
        app.setPublishedAt(LocalDateTime.now());
        appRepository.save(app);
        appDto = appMapper.toDto(app);
        hashOperations.put(RedisCacheKeyConstants.APP_HASH_KEY, app.getId(), appDto);
        zSetOperations.add(RedisCacheKeyConstants.APPS_ZSET_KEY, app.getId(), app.getId().doubleValue());
        return appDto;
    }

    @Transactional
    public AppDto updateApp(Integer id, AppDto appDto) {
        App app = appRepository.findById(id).orElseThrow(AppNotFoundException::new);
        Category category = categoryRepository.findById(appDto.getCategory().getId())
                .orElseThrow(CategoryNotFoundException::new);
        app = appMapper.toEntity(appDto, app);
        app.setCategory(category);
        appRepository.save(app);
        appDto = appMapper.toDto(app);
        // this element will be cache like app::{userId} (app::1)
        hashOperations.put(RedisCacheKeyConstants.APP_HASH_KEY, app.getId(), appDto);
        return appDto;
    }

    @Transactional
    public void deleteApp(Integer id) {
        App app = appRepository.findById(id).orElseThrow(AppNotFoundException::new);
        downloaderRepository.deleteAllByApp(app);
        appRepository.delete(app);
        hashOperations.delete(RedisCacheKeyConstants.APP_HASH_KEY, id);
        String key = buildDownloadsKey(id);
        redisTemplate.delete(key);
        zSetOperations.remove(RedisCacheKeyConstants.APPS_ZSET_KEY, app.getId());
    }

    @Transactional
    public Boolean downloadApp(Integer appId, Integer userId) {
        App app = appRepository.findById(appId).orElseThrow(AppNotFoundException::new);
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        String key = buildDownloadsKey(appId);
        Long index = listOperations.indexOf(key, user.getId());
        if (index != null && index > -1) {
            return false;
        }
        downloaderRepository.save(Downloader.builder()
                .user(user)
                .app(app)
                .downloadedAt(LocalDateTime.now())
                .build());
        listOperations.rightPush(key, user.getId());
        return true;
    }

    public PageDto<UserDto> getDownloads(Integer appId, Pageable pageable) {
        App app = appRepository.findById(appId).orElseThrow(AppNotFoundException::new);
        String key = buildDownloadsKey(app.getId());
        List<Object> userIds = listOperations.range(key, pageable.getOffset(),
                pageable.getOffset() + pageable.getPageSize() - 1);
        if (userIds != null) {
            List<UserDto> users = userIds.stream()
                    .map(userId -> (UserDto) hashOperations.get(RedisCacheKeyConstants.USER_HASH_KEY, userId))
                    .collect(Collectors.toList());
            Long count = listOperations.size(key);
            return PageDto.of(users, pageable.getPageNumber(), pageable.getPageSize(), count);
        }
        return PageDto.empty();
    }

    private List<Category> createCategories() {
        return categoryRepository.saveAll(List.of(
                Category.builder()
                        .name("Games")
                        .build(),
                Category.builder()
                        .name("Dating")
                        .build(),
                Category.builder()
                        .name("Music")
                        .build(),
                Category.builder()
                        .name("Social")
                        .build(),
                Category.builder()
                        .name("Food&Drink")
                        .build()
        ));
    }

    private List<User> createUsers(Faker faker) {
        List<User> users = new ArrayList<>();
        IntStream.range(0, 200).forEach(k -> {
            User user = User.builder()
                    .name(faker.name().firstName())
                    .surname(faker.name().lastName())
                    .build();
            users.add(userRepository.save(user));
            hashOperations.put(RedisCacheKeyConstants.USER_HASH_KEY, user.getId(), userMapper.toDto(user));
        });
        return users;
    }

    private List<App> createApps(Faker faker, List<Category> categories) {
        List<App> apps = new ArrayList<>();
        IntStream.range(0, 100).forEach(i -> {
            int randomCategoryIndex = random.nextInt(categories.size());
            App app = App.builder()
                    .name(faker.app().name())
                    .author(faker.app().author())
                    .category(categories.get(randomCategoryIndex))
                    .description(faker.lorem().sentence(5))
                    .publishedAt(LocalDateTime.now())
                    .build();
            apps.add(appRepository.save(app));
            hashOperations.put(RedisCacheKeyConstants.APP_HASH_KEY, app.getId(), appMapper.toDto(app));
            zSetOperations.add(RedisCacheKeyConstants.APPS_ZSET_KEY, app.getId(), app.getId().doubleValue());
        });
        return apps;
    }

    private void createDownloads(List<App> apps, List<User> users) {
        IntStream.range(0, apps.size()).forEach(i -> {
            int randomEndBetween0And100 = random.nextInt(100);
            App app = apps.get(i);
            IntStream.range(0, users.size() - randomEndBetween0And100).forEach(k -> {
                Downloader downloader = Downloader.builder()
                        .app(app)
                        .user(users.get(k))
                        .downloadedAt(LocalDateTime.now())
                        .build();
                downloaderRepository.save(downloader);
                String key = buildDownloadsKey(app.getId());
                listOperations.rightPush(key, downloader.getUser().getId());
            });
        });
    }

    private String buildDownloadsKey(Integer appId) {
        return RedisCacheKeyConstants.DOWNLOADERS_LIST_KEY.replace("{appId}", appId.toString());
    }

}
