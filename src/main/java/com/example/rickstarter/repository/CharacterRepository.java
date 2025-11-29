package com.example.rickstarter.repository;

import com.example.rickstarter.model.Character;
import com.example.rickstarter.model.PageResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CharacterRepository {

    private static final String STORAGE_FILE = "characters.json";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Long nextId;

    public CharacterRepository() {
        this.nextId = loadNextId();
    }

    public PageResponse<Character> findAll(int page, int size) {
        List<Character> all = loadAll();
        int totalElements = all.size();
        int fromIndex = page * size;
        if (fromIndex >= totalElements) {
            PageResponse<Character> empty = new PageResponse<>();
            empty.setContent(Collections.emptyList());
            empty.setPage(page);
            empty.setSize(size);
            empty.setTotalElements(totalElements);
            empty.setTotalPages(calculateTotalPages(totalElements, size));
            return empty;
        }
        int toIndex = Math.min(fromIndex + size, totalElements);
        List<Character> content = all.subList(fromIndex, toIndex);
        PageResponse<Character> response = new PageResponse<>();
        response.setContent(new ArrayList<>(content));
        response.setPage(page);
        response.setSize(size);
        response.setTotalElements(totalElements);
        response.setTotalPages(calculateTotalPages(totalElements, size));
        return response;
    }

    public Optional<Character> findById(Long id) {
        return loadAll().stream().filter(c -> c.getId().equals(id)).findFirst();
    }

    public Character save(Character character) {
        List<Character> all = loadAll();
        if (character.getId() == null) {
            character.setId(generateId());
            all.add(character);
        } else {
            all.removeIf(c -> c.getId().equals(character.getId()));
            all.add(character);
        }
        persistAll(all);
        return character;
    }

    public void deleteById(Long id) {
        List<Character> all = loadAll();
        all.removeIf(c -> c.getId().equals(id));
        persistAll(all);
    }

    public void deleteAll() {
        persistAll(new ArrayList<>());
        this.nextId = 1L;
    }

    public void saveAll(List<Character> characters) {
        List<Character> all = loadAll();
        for (Character character : characters) {
            if (character.getId() == null) {
                character.setId(generateId());
            } else if (character.getId() >= nextId) {
                nextId = character.getId() + 1;
            }
            all.removeIf(c -> c.getId().equals(character.getId()));
            all.add(character);
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
        List<Character> all = loadAll();
        Long max = 0L;
        for (Character c : all) {
            if (c.getId() != null && c.getId() > max) {
                max = c.getId();
            }
        }
        return max + 1;
    }

    private List<Character> loadAll() {
        try {
            File file = new File(STORAGE_FILE);
            if (!file.exists() || Files.size(file.toPath()) == 0) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(file, new TypeReference<List<Character>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void persistAll(List<Character> characters) {
        try {
            File file = new File(STORAGE_FILE);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, characters);
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