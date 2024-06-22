package com.event.eventapp.service;

import com.event.eventapp.DTO.SearchResultDTO;
import com.event.eventapp.model.Location;
import com.event.eventapp.repository.LocationRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LocationService {
    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public List<Location> findAllLocations() {
        return locationRepository.findAll();
    }

    public Location findById(Long locationId) {
        return locationRepository.findLocationById(locationId);
    }

    public Set<Location> findByIds(Set<Long> ids) {
        return new HashSet<>(locationRepository.findAllById(ids));
    }

}
