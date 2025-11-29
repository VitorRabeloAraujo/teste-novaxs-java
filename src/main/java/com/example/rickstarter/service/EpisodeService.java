package com.example.rickstarter.service;

import com.example.rickstarter.model.Episode;
import com.example.rickstarter.model.PageResponse;
import com.example.rickstarter.repository.EpisodeRepository;

import java.util.List;
import java.util.Optional;

public class EpisodeService {

    private final EpisodeRepository repository;

    public EpisodeService() {
        this.repository = new EpisodeRepository();
    }

    public PageResponse<Episode> list(int page, int size) {
        return repository.findAll(page, size);
    }

    public List<Episode> findAll() {
        PageResponse<Episode> response = repository.findAll(0, Integer.MAX_VALUE);
        return response.getContent();
    }

    public Optional<Episode> findById(Long id) {
        return repository.findById(id);
    }

    public Episode create(Episode episode) {
        episode.setId(null);
        return repository.save(episode);
    }

    public Optional<Episode> update(Long id, Episode episode) {
        Optional<Episode> existing = repository.findById(id);
        if (existing.isEmpty()) {
            return Optional.empty();
        }
        episode.setId(id);
        Episode saved = repository.save(episode);
        return Optional.of(saved);
    }

    public boolean delete(Long id) {
        Optional<Episode> existing = repository.findById(id);
        if (existing.isEmpty()) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public void saveAll(List<Episode> episodes) {
        repository.saveAll(episodes);
    }
}