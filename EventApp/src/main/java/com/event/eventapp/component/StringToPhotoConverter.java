package com.event.eventapp.component;

import com.event.eventapp.model.Photo;
import com.event.eventapp.repository.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class StringToPhotoConverter implements Converter<String, Set<Photo>> {
    @Autowired
    private PhotoRepository photoRepository;

    @Override
    public Set<Photo> convert(String source) {
        Set<Photo> photoSet = new HashSet<>();
        if (source != null && !source.isEmpty()) {
            String[] ids = source.split(",");
            for (String id : ids) {
                photoRepository.findById(Long.valueOf(id)).ifPresent(photoSet::add);
            }
        }
        return photoSet;
    }
}
