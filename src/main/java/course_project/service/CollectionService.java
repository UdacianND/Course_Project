package course_project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import course_project.entity.Collection;
import course_project.entity.Topic;
import course_project.entity.field.Field;
import course_project.entity.user.User;
import course_project.payload.request.CollectionFieldDto;
import course_project.payload.response.CollectionDto;
import course_project.payload.response.FieldDto;
import course_project.repository.CollectionRepository;
import course_project.repository.FieldRepository;
import course_project.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static course_project.entity.user.Role.ADMIN;

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
        String imageUrl = cloudinaryService.uploadFile(image);
        Collection collection = new Collection(
                name,
                description,
                getTopic(topic),
                imageUrl,
                getPrincipal()
        );
        Collection savedCollection = collectionRepository.save(collection);
        saveFields(savedCollection, fields);
    }

    private Topic getTopic(String name){
        Optional<Topic> optionalTopic = topicRepository.findByName(name);
        if(optionalTopic.isPresent())
            return optionalTopic.get();
        Topic newTopic = new Topic(null,name,null);
        return topicRepository.save(newTopic);
    }

    private void saveFields(Collection collection, String fields) throws JsonProcessingException {
        List<CollectionFieldDto> fieldList = objectMapper.readValue(fields, new TypeReference<>() {});
        for (CollectionFieldDto fieldDto:fieldList){
            Field field = new Field(fieldDto, collection);
            fieldRepository.save(field);
        }
    }

    public User getPrincipal(){
        return (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public String getUserCollections() throws JsonProcessingException {
        Long userId = getPrincipal().getId();
        List<Collection> collections = collectionRepository.findAllByUserId(userId);
        List<CollectionDto> collectionDtoList = new ArrayList<>();
        for (Collection collection: collections){
            collectionDtoList.add(
                    new CollectionDto(
                            collection.getId(), userId,
                            collection.getName(),
                            collection.getTopic().getName(),
                            collection.getDescription(),
                            collection.getImageUrl()));
        }
        return objectMapper.writeValueAsString(collectionDtoList);
    }

    public String getItemFields(Long collectionId) throws JsonProcessingException, IllegalStateException {
        authorize(collectionId);
        return objectMapper.writeValueAsString(getFields(collectionId));
    }

    public Collection getCollection(Long collectionId){
        Optional<Collection> optionalCollection = collectionRepository.findById(collectionId);
        if(optionalCollection.isEmpty())
            throw new NullPointerException();
        return optionalCollection.get();
    }

    public Collection authorize(Long collectionId){
        Collection collection = getCollection(collectionId);
        User principal = getPrincipal();
        boolean isAuthorized = Objects.equals(collection.getUser().getId(), principal.getId())
                || principal.getRole() == ADMIN;
        if(!isAuthorized)
            throw new IllegalStateException("Not authorised");
        return collection;
    }

    private List<FieldDto> getFields(Long collectionId){
        List<Field> fields = fieldRepository.findAllByCollection_Id(collectionId);
        List<FieldDto> fieldDtoList = new ArrayList<>();
        for (Field field : fields){
            fieldDtoList.add( new FieldDto(
                    field.getId(),
                    field.getName(),
                    field.getType().toString().toLowerCase(), ""));
        }
        return fieldDtoList;
    }

    public String getCollectionInfo(Long collectionId) throws JsonProcessingException {
        Collection collection = getCollection(collectionId);
        CollectionDto collectionDto = new CollectionDto(
                collection.getId(),
                collection.getUser().getId(),
                collection.getName(),
                collection.getTopic().getName(),
                collection.getDescription(),
                collection.getImageUrl());
        return objectMapper.writeValueAsString(collectionDto);
    }

    public String getTopics(String name) throws JsonProcessingException {
        List<Topic> topics = topicRepository.getTopicsByName(name);
        List<String> topicNames = new ArrayList<>();
        for (Topic topic: topics) {
            topicNames.add(topic.getName());
        }
        return objectMapper.writeValueAsString(topicNames);
    }

    public void editCollection(Long id, String name, String topicName, MultipartFile image, String description) {
        Collection collection = authorize(id);
        String imageUrl = cloudinaryService.uploadFile(image);
        Topic topic = getTopic(topicName);
        collection.setName(name);
        collection.setImageUrl(imageUrl);
        collection.setTopic(topic);
        collection.setDescription(description);
        collectionRepository.save(collection);
    }

    @Transactional
    public void deleteCollection(Collection collection){
        fieldRepository.deleteAllByCollection_Id(collection.getId());
        collectionRepository.delete(collection);
    }
}
