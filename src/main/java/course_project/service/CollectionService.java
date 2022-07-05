package course_project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import course_project.entity.Collection;
import course_project.entity.Topic;
import course_project.entity.field.Field;
import course_project.payload.request.CollectionFieldDto;
import course_project.repository.CollectionRepository;
import course_project.repository.FieldRepository;
import course_project.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CollectionService {
    private final CloudinaryService cloudinaryService;
    private final TopicRepository topicRepository;
    private final FieldRepository fieldRepository;
    private final ObjectMapper objectMapper;
    private final CollectionRepository collectionRepository;

    public void addCollection(
            String name,
            String topic,
            MultipartFile image,
            String description,
            String fields
    ) throws JsonProcessingException {
        String url = cloudinaryService.uploadFile(image);
        Collection collection = new Collection(
                null,
                name,
                description,
                getTopic(topic),
                url
        );
        Collection savedCollection = collectionRepository.save(collection);
        saveFields(savedCollection, fields);
    }

    private Topic getTopic(String name){
        Optional<Topic> optionalTopic = topicRepository.findByName(name);
        if(optionalTopic.isPresent())
            return optionalTopic.get();
        Topic newTopic = new Topic(null,name);
        return topicRepository.save(newTopic);
    }

    private void saveFields(Collection collection, String fields) throws JsonProcessingException {
        List<CollectionFieldDto> fieldList = objectMapper.readValue(fields, new TypeReference<>() {});
        for (CollectionFieldDto fieldDto:fieldList){
            Field field = new Field(fieldDto, collection);
            fieldRepository.save(field);
        }
    }
}
