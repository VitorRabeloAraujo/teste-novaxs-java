package com.example.rickstarter.service;

import com.example.rickstarter.model.Character;
import com.example.rickstarter.model.PageResponse;
import com.example.rickstarter.repository.CharacterRepository;

import java.util.List;
import java.util.Optional;

public class CharacterService {

    private final CharacterRepository repository;

    public CharacterService() {
        this.repository = new CharacterRepository();
    }

    public PageResponse<Character> list(int page, int size) {
        return repository.findAll(page, size);
    }

    public Optional<Character> findById(Long id) {
        return repository.findById(id);
    }

    public Character create(Character character) {
        character.setId(null);
        return repository.save(character);
    }

    public Optional<Character> update(Long id, Character character) {
        Optional<Character> existing = repository.findById(id);
        if (existing.isEmpty()) {
            return Optional.empty();
        }
        character.setId(id);
        Character saved = repository.save(character);
        return Optional.of(saved);
    }

    public boolean delete(Long id) {
        Optional<Character> existing = repository.findById(id);
        if (existing.isEmpty()) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }

    public void deleteAll() {
        repository.deleteAll();
    }

    public void saveAll(List<Character> characters) {
        repository.saveAll(characters);
    }
}