package com.example.rickstarter.repository;

import com.example.rickstarter.model.Episode;
import com.example.rickstarter.model.PageResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class EpisodeRepository {

    private static final String STORAGE_FILE = "episodes.json";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Long nextId;

    public EpisodeRepository() {
        this.nextId = loadNextId();
    }

    public PageResponse<Episode> findAll(int page, int size) {
        List<Episode> all = loadAll();
        int totalElements = all.size();
        int fromIndex = page * size;
        if (fromIndex >= totalElements) {
            PageResponse<Episode> empty = new PageResponse<>();
            empty.setContent(Collections.emptyList());
            empty.setPage(page);
            empty.setSize(size);
            empty.setTotalElements(totalElements);
            empty.setTotalPages(calculateTotalPages(totalElements, size));
            return empty;
        }
        int toIndex = Math.min(fromIndex + size, totalElements);
        List<Episode> content = all.subList(fromIndex, toIndex);
        PageResponse<Episode> response = new PageResponse<>();
        response.setContent(new ArrayList<>(content));
        response.setPage(page);
        response.setSize(size);
        response.setTotalElements(totalElements);
        response.setTotalPages(calculateTotalPages(totalElements, size));
        return response;
    }

    public Optional<Episode> findById(Long id) {
        return loadAll().stream().filter(e -> e.getId().equals(id)).findFirst();
    }

    public Episode save(Episode episode) {
        List<Episode> all = loadAll();
        if (episode.getId() == null) {
            episode.setId(generateId());
            all.add(episode);
        } else {
            all.removeIf(e -> e.getId().equals(episode.getId()));
            all.add(episode);
        }
        persistAll(all);
        return episode;
    }

    public void deleteById(Long id) {
        List<Episode> all = loadAll();
        all.removeIf(e -> e.getId().equals(id));
        persistAll(all);
    }

    public void deleteAll() {
        persistAll(new ArrayList<>());
        this.nextId = 1L;
    }

    public void saveAll(List<Episode> episodes) {
        List<Episode> all = loadAll();
        for (Episode episode : episodes) {
            if (episode.getId() == null) {
                episode.setId(generateId());
            } else if (episode.getId() >= nextId) {
                nextId = episode.getId() + 1;
            }
            all.removeIf(e -> e.getId().equals(episode.getId()));
            all.add(episode);
        }
        persistAll(all);
    }

    private synchronized Long generateId() {
        if (nextId == null) {
            nextId = loadNextId();
        }
        Long current = nextId;
        nextId = nextId + 1;
        return current;
    }

    private Long loadNextId() {
        List<Episode> all = loadAll();
        Long max = 0L;
        for (Episode e : all) {
            if (e.getId() != null && e.getId() > max) {
                max = e.getId();
            }
        }
        return max + 1;
    }

    private List<Episode> loadAll() {
        try {
            File file = new File(STORAGE_FILE);
            if (!file.exists() || Files.size(file.toPath()) == 0) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(file, new TypeReference<List<Episode>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void persistAll(List<Episode> episodes) {
        try {
            File file = new File(STORAGE_FILE);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, episodes);
        } catch (Exception e) {
        }
    }

    private int calculateTotalPages(long totalElements, int size) {
        if (size <= 0) {
            return 1;
        }
        return (int) Math.ceil((double) totalElements / (double) size);
    }
}